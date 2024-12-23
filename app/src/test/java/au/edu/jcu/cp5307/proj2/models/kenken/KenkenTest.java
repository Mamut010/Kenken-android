package au.edu.jcu.cp5307.proj2.models.kenken;

import static org.junit.Assert.*;

import org.junit.Test;

import java.util.List;

import au.edu.jcu.cp5307.proj2.application.kenken.KenkenGame;

public class KenkenTest {

    @Test
    public void parseFromDescriptions_should_success_after_stringified() {
        List<String> descriptions = List.of(
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
            """
        );

        List<KenkenGame> originalKenkens = descriptions
                .stream()
                .map(KenkenGame::parseFromDescriptions)
                .toList();
        List<String> stringifiedKenkens = originalKenkens
                .stream()
                .map(KenkenGame::toString)
                .toList();
        List<KenkenGame> restoredKenkens = stringifiedKenkens
                .stream()
                .map(KenkenGame::parseFromDescriptions)
                .toList();

        boolean same = restoredKenkens
                .stream()
                .allMatch(restored -> originalKenkens
                        .stream()
                        .anyMatch(restored::equals)
                );
        assertTrue(same);
    }

    @Test
    public void testEquals_same_description_should_identical() {
        String description =
            """
            3 + A1 A2; 3 = A3;
            3 = B1; 4 + B2 B3 C3;
            5 + C1 C2
            """;
        KenkenGame kenken1 = KenkenGame.parseFromDescriptions(description);
        KenkenGame kenken2 = KenkenGame.parseFromDescriptions(description);
        assertEquals(kenken1, kenken2);
    }
}