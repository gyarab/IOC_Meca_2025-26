# IOC_Meca_2025-26

Tento repozitář obsahuje zdrojové soubory, strukturu projektu v textovém souboru sources.txt a obrázky k mojí ročníkové práci (IOČ).

# Spuštění programu

1) nejprve je potřeba stáhnout všechny třídy s obrázky figurek a následně je spustit v nějakém IDE (např. NetBeans, Apache Netbeans či JetBrains)
2) poté je třeba nastavit jako hlavní třídu Chesswindow, která obsahuje kód pro vykreslení hlavního okna programu
3) pak se program jednoduše spustí např. v editoru třídy Chesswindow pomocí klávesové zkratky Shift + F6 nebo stisknutím pravého tlačítka na daný soubor (v tomto případě třída Chesswindow) v okně projektu ("Projects") a zvolením "Run File", kde si mj. můžete všimnout, že je k ní uvedená také výše zmíněná klávesová zkratka
4) nakonec je třeba chvíli počkat než se spustí vlákno a zavolají se příslušné třídy s metodami

# Počítačová simulace šachové partie proti umělé inteligenci 

Ročníkový projekt napsaný v Javě, jehož cílem je vytvořit plnohodnotnou šachovou aplikaci s GUI, kde si hráč může zahrát proti AI. Aplikace umonžňuje uživateli detekovat všechny herní stavy včetně speciálních tahů a ukončovacích podmínek. Aplikace bude mít vizuálně přivětivé prostředí, které bude zábavné a srozumitelné i pro začátečníky. 

# Postup implementace

Navázání na stávající šachový program, který byl použit jako ročníkový projekt v rámci předmětu programovaní v 1. ročníku v jazyce Java 8 a prostředí NetBeans IDE 8.2. 

Přidání časomíry - vytvoření hodin, které zaznamenávají a spravují časy obou hráčů s možností nastavení limitů. 

Integrace šachového bota (AI) - přidání logiky pro hraní počítačem. Boti budou schopni vyhodnocovat pozice pomocí evaluačních funkcí a volit nejlepší tahy v závislosti na zvolené hloubce prohledávání podle algoritmů MiniMax, 
popř. pomocí vylepšeného algoritmu Minimax s Alfa-betou ořezávání. 

Přidání vizuálních efektů – zvýraznění možných tahů, efekty při braní figur, proměně pěšce, apod.

Bonus:


Implementace remízových stavů mimo patu – pravidlo 50 tahů a detekce nedostatku materiálu.


Možná nabídka manuální remízy hráčem ve druhém tahu (z pohledu bílého) a možnost se vzdát po prvních dvou tazích.

# Doporučené zdroje k IOČ

- https://www.freecodecamp.org/news/simple-chess-ai-step-by-step-1d55a9266977/
- https://stackoverflow.com/questions/59708531/chess-ai-implementation-in-java-project
- https://github.com/Virksaabnavjot/Chess-AI
- http://www.chesstantra.com/chess-facts/
- https://is.slu.cz/th/zi4wd/FPF_DP_21_Algoritmus_Minimax_a_alfa-beta_orezavani_Juricek_Martin.pdf?lang=cs
- https://is.muni.cz/th/aiw7t/bapr_digital.pdf?kodomez=termin-540379;lang=cs
- https://www.youtube.com/playlist?list=PLOJzCFLZdG4zk5d-1_ah2B4kqZSeIlWtt
- https://github.com/lhartikk/simple-chess-ai
- https://www.geeksforgeeks.org/dsa/backtracking-algorithms/
-https://portal.matematickabiologie.cz/index.php?pg=analyza-a-hodnoceni-biologickych-dat--vicerozmerne-metody-pro-analyzu-dat--volba-a-vyber-popisnych-promennych--selekce-promennych--algoritmy-selekce-promennych--algoritmus-min-max
- https://ksvi.mff.cuni.cz/~dvorak/vyuka/NUIN017/Minimax.pdf
- https://nlp.fi.muni.cz/uui/slajdy-2014/uui07-2.pdf
- https://naos-be.zcu.cz/server/api/core/bitstreams/19ae03fc-5a48-4421-9249-9f0bf08a55fc/content
- https://www.chessprogramming.org/Simplified_Evaluation_Function
- https://github.com/amir650/BlackWidow-Chess
- https://www.youtube.com/playlist?list=PLOJzCFLZdG4zk5d-1_ah2B4kqZSeIlWtt
- https://www.inf.upol.cz/downloads/studium/PS/minimax.pdf
- https://portal.matematickabiologie.cz/index.php?pg=analyza-a-hodnoceni-biologickych-dat--vicerozmerne-metody-pro-analyzu-dat--volba-a-vyber-popisnych-promennych--selekce-promennych--algoritmy-selekce-promennych--algoritmus-min-max
- https://kam.fit.cvut.cz/bi-zum/media/lectures/09-minimax-v5.0-noanim.pdf
- https://ksvi.mff.cuni.cz/~dvorak/vyuka/NUIN017/Minimax.pdf
- https://www.youtube.com/watch?v=NWHIyTRUsMg
- https://en.wikipedia.org/wiki/Computer_chess

Obrázky:
- https://i.sstatic.net/hxGdi.png

# Dokumentace k projektu
- **[Dokumentace_šachový program s AI_meca](https://docs.google.com/document/d/1e2D7Z-hu4OAku_vGMSUD6-O09_C6q0FpCqjCMMv7Eqg/edit?tab=t.0)**
