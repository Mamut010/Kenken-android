package au.edu.jcu.cp5307.proj2.dal.datasource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import au.edu.jcu.cp5307.proj2.models.Kenken;
import au.edu.jcu.cp5307.proj2.application.kenken.KenkenGame;

public class KenkenInMemoryDataSource {
    private final Map<Integer, Kenken> idKenkenMap;

    public KenkenInMemoryDataSource() {
        idKenkenMap = new HashMap<>();
        String[] descriptions = {
                """
            1 = A1
            """,
                """
            2 × A1 B1;
            1 - A2 B2
            """,
                """
            3 + A1 B1;
            5 + A2 B2;
            1 = A3;
            4 + C1 C2;
            5 + B3 C3;
            """,
                """
            3 = A1;
            1 - A2 A3;
            1 - B1 C1;
            2 = B2;
            7 + B3 C2 C3;
            """,
                """
            3 + A1 A2; 3 = A3;
            3 = B1; 4 + B2 B3 C3;
            5 + C1 C2
            """,
                """
            7 + A1 B1; 2 / C1 D1;
            1 - A2 A3; 3 - B2 B3;
            2 / A4 B4; 3 = C2;
            12 × C3 C4 D4; 2 / D2 D3
            """,
                """
            1 - a1 a2; 2 / a3 b3;
            5 + a4 b4; 2 / b1 c1;
            5 + b2 c2; 1 - c3 d3;
            6 x c4 d4; 4 x d1 d2
            """,
                """
            48 x a1 a2 a3 b1; 5 + a4 b4;
            2 / b2 c2; 12 x b3 c3;
            5 + c1 d1; 2 - d2 d3; 1 - c4 d4
            """,
                """
            12 × a1 b1; 2 - a2 b2;
            4 - a3 a4; 2 / a5 b5;
            2 / b3 b4; 2 / c1 c2;
            15 + c3 c4 c5 d3;
            3 - d1 e1; 16 × d2 e2 e3;
            1 - d4 e4; 2 - d5 e5
            """,
                """
            8 + a1 b1 c1; 2 - a2 a3;
            4 × a4 a5 b5; 2 / b2 b3;
            1 - b4 c4; 3 = c5;
            4 - c2 d2; 10 + c3 d3 d4;
            48 × d1 e1 e2; 2 / e3 e4;
            3 - d5 e5
            """,
                """
            3 - a1 a2; 1 - a3 a4;
            15 × a5 b5 b4; 12 × b1 b2 c2;
            10 + b3 c3 d3; 1 = c1; 2 / c4 c5;
            2 / d1 d2; 20 × d4 d5 e5;
            8 + e1 e2; 3 + e3 e4
            """,
                """
            11 + A1 B1; 2 / A2 A3; 20 × A4 B4; 6 × A5 A6 B6 C6;
            3 - B2 B3; 3 / B5 C5;
            240 × C1 C2 D1 D2; 6 × C3 C4;
            6 × D3 E3; 7 + D4 E4 E5; 30 × D5 D6;
            6 × E1 E2; 9 + E6 F6;
            8 + F1 F2 F3; 2 / F4 F5
            """,
                """
            3 - a1 b1; 108 × a2 a3 b3; 13 + a4 b4 b5; 2 / a5 a6; 13 + a7 b6 b7;
            3 - b2 c2; 70 × c1 d1 e1; 5 = d2; 504 × c3 c4 d3 e3 e4; 60 × c5 d4 d5 e5;
            4 - c6 c7; 1 - d6 d7; 6 - e6 e7; 2 / f1 g1; 2 / g2 g3; 30 × e2 f2 f3;
            140 × f4 f5 g4; 1 - g5 g6; 14 + f6 f7 g7
            """,
                """
            3 = a1; 15 + a2 a3 b3; 12 + a4 a5 a6; 1 - a7 b7;
            6 / b1 b2; 7 + b4 b5; 7 = b6; 8 × c1 c2; 2 / c3 c4; 35 × c5 c6 c7;
            11 + d1 e1 e2; 5 - d2 d3; 3 / d4 e4; 30 × d5 d6; 9 + d7 e7; 2 - e5 e6;
            8 + e3 f3 g3; 24 × f1 f2; 35 × g1 g2; 10 + f4 f5; 2 × f6 f7; 6 + g4 g5; 3 - g6 g7
            """,
                """
            3 / a1 a2; 9 + a3 a4; 210 × a5 b5 c5 b4 c4; 19 + a6 b6 c6; 90 × a7 a8 b7;
            10 × b1 b2; 8 - b3; 4 / b8 c8;
            7 + c1 d1; 7 = c2; 1 - c3 d3; 4 = c7; 2 / d2 e2; 17 + d4 e4 f4; 6 / d5 d6; 6 × d7 d8;
            16 + e1 f1 f2 g2; 7 = e3; 5 - e5 e6; 12 × e7 e8 f8; 7 + f3 g3; 80 × f5 f6 f7;
            8 / g1 h1; 4 - h2 h3; 1 - g4 h4; 15 + g5 h5 h6; 1 = g6; 12 + g7 h7; 1 - g8 h8
            """,
                """
            10 + A1 B1; 7 * A2 A3; 13 + A4 A5 B4; 2 / A6  A7; 12 + A8 B7 B8; 63 * A9 B9;
            15 * B2 B3; 144 * B5 B6 C5;
            6 * C1 D1 E1; 4 - C2 C3; 8 + C4 D4; 22 + C6 D5 D6; 2 / C7 D7; 5 + C8 C9;
            25 + D2 D3 E2 E3; 1 - D8 E8; 11 + D9 E9;
            36 * E4 F4; 15 * E5 F5; 4 - E6 E7;
            2 - F1 G1; 6 - F2 F3; 2 / F6 F7; 56 * F8 F9 G8 G9;
            9 * G2 G3; 10 + G4 G5; 24 * G6 G7;
            1 - H1 I1; 1 - H2 I2; 3 / H3 I3; 3 - H4 I4; 5 - H5 I5; 35 * H6 I6; 5 - H7 H8; 9 + H9 I9;
            4 - I7 I8
            """,
        };
        for (int i = 0; i < descriptions.length; i++) {
            int id = i + 1;
            idKenkenMap.put(id, new Kenken(id, KenkenGame.parseFromDescriptions(descriptions[i])));
        }
    }

    public List<Kenken> fetchAll() {
        return new ArrayList<>(idKenkenMap.values());
    }

    public Kenken fetchById(int id) {
        return idKenkenMap.get(id);
    }
}
