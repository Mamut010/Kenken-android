package au.edu.jcu.cp5307.proj2.ui.fragments;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.InsetDrawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import au.edu.jcu.cp5307.proj2.R;
import au.edu.jcu.cp5307.proj2.constants.Constants;
import au.edu.jcu.cp5307.proj2.application.kenken.KenkenGame;
import au.edu.jcu.cp5307.proj2.ui.utils.GameTableCell;
import au.edu.jcu.cp5307.proj2.utils.AsyncTaskExecutorService;
import au.edu.jcu.cp5307.proj2.utils.Converters;
import au.edu.jcu.cp5307.proj2.utils.Pair;
import au.edu.jcu.cp5307.proj2.utils.Point;
import au.edu.jcu.cp5307.proj2.utils.helpers.UiHelpers;
import au.edu.jcu.cp5307.proj2.viewmodels.GameViewModel;

public class GameGridFragment extends Fragment {
    public interface GameLoadListener {
        void onPreGameLoad();
        void onPostGameLoad(KenkenGame kenken);
    }

    private static final String GRID_INTERACTION_ENABLED_KEY = "GRID_INTERACTION_ENABLED";
    private static final String PRELOAD_ALPHA_KEY = "PRELOAD_ALPHA";
    private static final int POINT_SMALLEST_COORDINATE = 1;

    private TableLayout gameLayout;
    private Map<Point, GameTableCell> pointCellMap;
    private GameViewModel viewModel;
    private GameLoadListener listener;
    private GridBuildAsyncTask task;
    private boolean isGridInteractionEnabled;
    private float preloadAlpha;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initNonViewFields();
        if (savedInstanceState != null) {
            restoreFromSavedState(savedInstanceState);
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof GameLoadListener) {
            listener = (GameLoadListener) context;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        task.cancel();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_game_grid, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putBoolean(GRID_INTERACTION_ENABLED_KEY, isGridInteractionEnabled);
        outState.putFloat(PRELOAD_ALPHA_KEY, preloadAlpha);
    }

    public void setViewModel(GameViewModel viewModel) {
        clearObservers();
        if (viewModel == null) {
            this.viewModel = null;
            task.cancel();
            clearGrid();
        }
        else {
            this.viewModel = viewModel;
            setupObservers();
        }
    }

    public void setGridInteraction(boolean enabled, float alpha) {
        isGridInteractionEnabled = enabled;
        preloadAlpha = alpha;
        UiHelpers.consumeViewGroupAndChildren(gameLayout, v -> {
            v.setEnabled(enabled);
            v.setAlpha(alpha);
        });
    }

    public void setGridInteraction(boolean enabled) {
        float alpha = enabled ? Constants.Ui.ALPHA_CLEAR : Constants.Ui.ALPHA_DIM;
        setGridInteraction(enabled, alpha);
    }

    private void initNonViewFields() {
        pointCellMap = new HashMap<>();
        isGridInteractionEnabled = true;
        preloadAlpha = 1f;
        task = new GridBuildAsyncTask();
    }

    private void initViews(View view) {
        gameLayout = view.findViewById(R.id.game_layout);
    }

    private void restoreFromSavedState(Bundle savedInstanceState) {
        isGridInteractionEnabled = savedInstanceState.getBoolean(GRID_INTERACTION_ENABLED_KEY);
        preloadAlpha = savedInstanceState.getFloat(PRELOAD_ALPHA_KEY);
    }

    private void clearGrid() {
        gameLayout.removeAllViews();
        pointCellMap.clear();
    }

    private void clearObservers() {
        if (viewModel != null) {
            viewModel.getPointValueMap().removeObservers(this);
        }
    }

    private void setupObservers() {
        viewModel.getKenken().observe(this, kenken -> task.execute(kenken));
    }

    private void configCell(GameTableCell cell, Point point, KenkenGame kenken) {
        adjustTextScale(cell, kenken.getSize());
        attachListenerToCell(cell, point, kenken);
        setCellBorder(cell, point, kenken);
    }

    private void adjustTextScale(GameTableCell cell, int gridSize) {
        final int smallestGridSize = 1;
        final float decrementalFactor = 0.1f;
        final float initialTextScale = 2f;
        final float textScaleLowerBound = 1.3f;
        float deltaScale = (gridSize - smallestGridSize) * decrementalFactor;
        float finalScale = Math.max(initialTextScale - deltaScale, textScaleLowerBound);
        cell.setValueTextScale(finalScale);
    }

    private void attachListenerToCell(GameTableCell cell, Point point, KenkenGame kenken) {
        cell.setOnClickListener(view -> {
            Integer currentValue = viewModel.getPointValue(point);
            List<Integer> values = IntStream
                    .of(kenken.getValueRange())
                    .boxed()
                    .filter(v -> !v.equals(currentValue))
                    .collect(Collectors.toList());
            ValueSelectionDialogFragment<Integer> dialog = ValueSelectionDialogFragment.newInstance(values);
            String dialogTitle = getString(
                    R.string.cell_number_select_dialog_title_format,
                    viewModel.getSuperscriptInCageOfPoint(point)
            );
            dialog.setClearable(currentValue != null);
            dialog.setTitle(dialogTitle);
            dialog.setDialogListener(new ValueSelectionDialogFragment.ValueSelectionDialogListener<>() {
                @Override
                public void onValueClick(DialogFragment dialog, int position, Integer selectedValue) {
                    viewModel.updatePointValue(point, selectedValue);
                }

                @Override
                public void onClearButtonClick(DialogFragment dialog) {
                    viewModel.clearPointValue(point);
                }

                @Override
                public void onCancelButtonClick(DialogFragment dialog) {
                    // DO NOTHING
                }
            });
            dialog.show(getParentFragmentManager(), ValueSelectionDialogFragment.class.getName());
        });
    }

    private void setCellBorder(GameTableCell cell, Point point, KenkenGame kenken) {
        Drawable strokeHidingDrawable = determineStrokeHidingDrawable(point, kenken);
        LayerDrawable normalLayerList = new LayerDrawable(new Drawable[]{
                UiHelpers.getDrawable(requireContext(), R.drawable.game_cell_background_normal),
                strokeHidingDrawable
        });
        LayerDrawable pressedLayerList = new LayerDrawable(new Drawable[]{
                UiHelpers.getDrawable(requireContext(), R.drawable.game_cell_background_pressed),
                strokeHidingDrawable
        });
        StateListDrawable stateList = new StateListDrawable();
        int statePressed = android.R.attr.state_pressed;
        int stateNonPressed = -statePressed;
        stateList.addState(new int[] { stateNonPressed }, normalLayerList);
        stateList.addState(new int[] { statePressed }, pressedLayerList);
        cell.setBackground(stateList);
    }

    private Drawable determineStrokeHidingDrawable(Point point, KenkenGame kenken) {
        final int strokeWidthDp = 1;
        int strokeWidth = Converters.dpToPx(requireContext(), strokeWidthDp);
        int ignored = UiHelpers.getInsetStrokeIgnoredOffset(strokeWidth);

        int insetLeft, insetTop, insetRight, insetBottom;
        insetLeft = insetTop = insetRight = insetBottom = Constants.Ui.INSET_SHOWN;
        GameViewModel.NeighborInfo neighborInfo = viewModel.getNeighborInfo(point);
        if (neighborInfo.isLeftSameCage()) {
            insetLeft = ignored;
        }
        if (neighborInfo.isTopSameCage()) {
            insetTop = ignored;
        }
        if (neighborInfo.isRightSameCage()) {
            insetRight = ignored;
        }
        if (neighborInfo.isBottomSameCage()) {
            insetBottom = ignored;
        }

        Drawable edges = UiHelpers.getDrawable(requireContext(), R.drawable.solid_edges);
        int gridSize = kenken.getSize();
        if (isPointAtRear(point, gridSize)) {
            Drawable subEdges = createSubEdgesForOuterPoint(point, gridSize,
                    strokeWidth, UiHelpers.getColor(requireContext(), R.color.black));
            edges = new LayerDrawable(new Drawable[]{ edges, subEdges });
        }

        return new InsetDrawable(edges, insetLeft, insetTop, insetRight, insetBottom);
    }

    private Drawable createSubEdgesForOuterPoint(Point point, int gridSize,
                                                 int outerStrokeWidth, int outerStrokeColor) {
        GradientDrawable subEdges = new GradientDrawable();
        final int overlappingEdgeCount = 2;
        int strokeWidth = outerStrokeWidth * overlappingEdgeCount;
        int ignored = UiHelpers.getInsetStrokeIgnoredOffset(strokeWidth);
        subEdges.setShape(GradientDrawable.RECTANGLE);
        subEdges.setStroke(strokeWidth, outerStrokeColor);

        int insetLeft, insetTop, insetRight, insetBottom;
        insetLeft = insetTop = insetRight = insetBottom = ignored;

        if (point.row() == POINT_SMALLEST_COORDINATE) {
            insetTop = Constants.Ui.INSET_SHOWN;
        }
        if (point.row() == gridSize) {
            insetBottom = Constants.Ui.INSET_SHOWN;
        }
        if (point.column() == POINT_SMALLEST_COORDINATE) {
            insetLeft = Constants.Ui.INSET_SHOWN;
        }
        if (point.column() == gridSize) {
            insetRight = Constants.Ui.INSET_SHOWN;
        }

        return new InsetDrawable(subEdges, insetLeft, insetTop, insetRight, insetBottom);
    }

    private void updateViews(Map<Point, Integer> pointValueMap) {
        Map<Point, Boolean> pointCorrectedMap = viewModel.checkPointAnswer();
        renderCells(pointValueMap, pointCorrectedMap);
    }

    private void renderCells(Map<Point, Integer> pointValueMap, Map<Point, Boolean> pointCorrectedMap) {
        for (Map.Entry<Point, Integer> e : pointValueMap.entrySet()) {
            Point point = e.getKey();
            Integer value = e.getValue();
            Boolean corrected = pointCorrectedMap.get(point);
            int colorId = Boolean.FALSE.equals(corrected) ? R.color.light_red : R.color.black;
            int color = UiHelpers.getColor(requireContext(), colorId);
            String valueText = toCellValueText(value);
            GameTableCell cell = cellAt(point);
            if (cell != null) {
                cell.setValueText(valueText);
                cell.setValueTextColor(color);
            }
        }
    }

    private GameTableCell cellAt(Point point) {
        return pointCellMap.get(point);
    }

    private static String toCellValueText(Integer value) {
        return value != null ? String.valueOf(value) : Constants.Type.EMPTY_STRING;
    }

    private static boolean isPointAtRear(Point point, int gridSize) {
        return point.row() == POINT_SMALLEST_COORDINATE
                || point.row() == gridSize
                || point.column() == POINT_SMALLEST_COORDINATE
                || point.column() == gridSize;
    }

    private class GridBuildAsyncTask extends AsyncTaskExecutorService<KenkenGame, Void,
            Pair<KenkenGame, List<TableRow>>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            gameLayout.setVisibility(View.INVISIBLE);
            gameLayout.removeAllViews();
            pointCellMap.clear();
            viewModel.getPointValueMap().removeObserver(GameGridFragment.this::updateViews);
            if (listener != null) {
                listener.onPreGameLoad();
            }
        }

        @Override
        protected void onCancelled(Pair<KenkenGame, List<TableRow>> pair) {
            super.onCancelled(pair);
            if (gameLayout != null) {
                gameLayout.setVisibility(View.VISIBLE);
            }
        }

        @Override
        protected void onPostExecute(Pair<KenkenGame, List<TableRow>> pair) {
            super.onPostExecute(pair);
            pair.second().forEach(tr -> gameLayout.addView(tr));
            setGridInteraction(isGridInteractionEnabled, preloadAlpha);
            viewModel.getPointValueMap().observe(
                    GameGridFragment.this,
                    GameGridFragment.this::updateViews
            );
            if (listener != null) {
                listener.onPostGameLoad(pair.first());
            }
            gameLayout.setVisibility(View.VISIBLE);
        }

        @Override
        protected Pair<KenkenGame, List<TableRow>> doInBackground(KenkenGame kenken) {
            if (kenken == null) {
                return new Pair<>(null, Collections.emptyList());
            }

            int gridSize = kenken.getSize();
            Map<Point, String> firstPointWithSuperscripts = viewModel.getFirstPointWithSuperscripts();

            List<TableRow> tableRows = new ArrayList<>(gridSize);
            for (int i = 0; i < gridSize; i++) {
                TableRow tr = new TableRow(getContext());
                for (int j = 0; j < gridSize; j++) {
                    Point currentPoint = new Point(i + 1, j + 1);
                    String superscript = firstPointWithSuperscripts.getOrDefault(
                            currentPoint,
                            null
                    );
                    GameTableCell currentCell = new GameTableCell(getContext(), superscript);
                    configCell(currentCell, currentPoint, kenken);
                    pointCellMap.put(currentPoint, currentCell);
                    tr.addView(currentCell.getView());
                }
                tableRows.add(tr);
            }
            return new Pair<>(kenken, tableRows);
        }
    }
}