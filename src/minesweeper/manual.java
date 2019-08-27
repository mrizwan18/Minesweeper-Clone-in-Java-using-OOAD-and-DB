package minesweeper;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class manual implements ActionListener {

    DBMine db;
    JFrame frame;
    Timer t;
    JButton[] button;
    JLabel l1, l2, l1icon, l2icon;
    JTextField nameP;
    int[] matrix;
    boolean[] chaypi;
    JPanel grid;
    JPanel menu;
    int turn = 0;
    ArrayList<Integer> flagsPositions;      //flags array, where flags are placed
    boolean gameEnd;
    ArrayList<Player> leaderboard;
    int time = 0, flags = 10, starter = 0;
    JPanel outer;
    JButton enter;
    private javax.swing.JButton[] menuBtns;

    public manual() throws UnsupportedAudioFileException, IOException, MalformedURLException, LineUnavailableException {
        t = new Timer(1000, new Listener());
        Timer starter = new Timer(1000, new startListener());
        starter.start();
        leaderboard = new ArrayList<>();
        db = new DBMine();
        flagsPositions = new ArrayList<>();
        matrix = new int[81];
        chaypi = new boolean[81];
        poulateMatrix();
        String soundFile = "D:/U N I/OOAD/Minesweeper/src/Sounds/start1.wav";
        try {
            playSound(soundFile);
        } catch (UnsupportedAudioFileException ex) {
            Logger.getLogger(manual.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(manual.class.getName()).log(Level.SEVERE, null, ex);
        } catch (LineUnavailableException ex) {
            Logger.getLogger(manual.class.getName()).log(Level.SEVERE, null, ex);
        }
        Menu();
    }

    public void restartGame() throws UnsupportedAudioFileException, IOException, MalformedURLException, LineUnavailableException {
        flagsPositions.removeAll(flagsPositions);
        time = 0;
        turn = 0;
        flags = 10;
        gameEnd = false;
        poulateMatrix();
        frame.dispose();
        draw();
    }

    public void Menu() {
        JFrame menu = new JFrame("Welcome");
        menu.setSize(600, 500);
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        menu.setLocation(dim.width / 2 - menu.getSize().width / 2, dim.height / 2 - menu.getSize().height / 2);

        JPanel newP = new JPanel(new BorderLayout());
        JPanel north = new JPanel();
        JLabel t = new JLabel();
        t.setText("Enter Name: ");
        t.setSize(90, 20);
        nameP = new JTextField();
        nameP.setSize(200, 40);
        nameP.setColumns(20);
        north.add(t);
        north.add(nameP);
        enter = new JButton("Play");
        enter.setEnabled(false);
        enter.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                String name = nameP.getText();
                try {
                    if (!name.equals("")) {
                        draw();
                        menu.dispose();
                    }
                } catch (UnsupportedAudioFileException ex) {
                    Logger.getLogger(manual.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(manual.class.getName()).log(Level.SEVERE, null, ex);
                } catch (LineUnavailableException ex) {
                    Logger.getLogger(manual.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
        });
        north.add(enter);
        newP.add(north, BorderLayout.NORTH);
        leaderboard = db.showLeaderboard();
        JPanel center = new JPanel(new GridLayout(leaderboard.size() + 1, 3));
        JLabel id = new JLabel("    ID of Player");
        JLabel name = new JLabel("Name of Player");
        JLabel score = new JLabel("Score of Player  ");
        center.add(id);
        center.add(name);
        center.add(score);
        JLabel[] names = new JLabel[10];
        JLabel[] ids = new JLabel[10];
        JLabel[] scores = new JLabel[10];
        for (int i = 0; i < leaderboard.size(); i++) {
            names[i] = new JLabel();
            ids[i] = new JLabel();
            scores[i] = new JLabel();
            ids[i].setText("         " + leaderboard.get(i).getId() + "");
            names[i].setText(leaderboard.get(i).getName());
            scores[i].setText(leaderboard.get(i).getScore());
            center.add(ids[i]);
            center.add(names[i]);
            center.add(scores[i]);
        }
        newP.add(center, BorderLayout.CENTER);

        menu.add(newP);
        menu.setVisible(true);
        menu.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public boolean remPos() {
        for (int i = 0; i < 81; i++) {
            if (chaypi[i] == false && matrix[i] != -1) {
                return false;
            }
        }
        return true;
    }

    public boolean remainingRevealed() {
        int rem = 0;

        for (int i = 0; i < 81; i++) {
            if (chaypi[i] == false) {
                rem++;
            }
        }
        if (rem == 10) {
            return remPos();
        }
        return false;
    }

    public void draw() throws UnsupportedAudioFileException, IOException, MalformedURLException, LineUnavailableException {

        frame = new JFrame("MineSweeper: Literally Mine-Sweeper");
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setLocation(dim.width / 2 - frame.getSize().width / 2, dim.height / 4 - frame.getSize().height / 2);
        outer = new JPanel(new BorderLayout());
        button = new JButton[81];
        grid = new JPanel(new GridLayout(9, 9, 0, 0));
        menu = new JPanel();
        JPanel info = new JPanel();
        JButton cheat = new JButton("Cheat it!");
        cheat.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                JFrame cheater = new JFrame();
                JPanel newP = new JPanel();
                JLabel t = new JLabel();
                t.setText("Enter Code: ");
                t.setSize(90, 20);
                JTextField code = new JTextField();
                code.setSize(200, 40);
                code.setColumns(20);
                newP.add(t);
                newP.add(code);
                JButton enter = new JButton("Enter");
                enter.addActionListener(new ActionListener() {

                    @Override
                    public void actionPerformed(ActionEvent e) {
                        String cc = code.getText().toString();
                        if (cc.equals("DUS POORAY AAP K")) {
                            cheatReveal();

                        }
                        cheater.dispose();
                    }
                });
                newP.add(enter);
                cheater.add(newP);
                cheater.setSize(400, 150);
                cheater.setVisible(true);
            }
        });

        JButton leader = new JButton("Leader Board");
        leader.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                leaderboard = db.showLeaderboard();
                JFrame newP = new JFrame("Scoreboard");
                JPanel center = new JPanel(new GridLayout(leaderboard.size() + 1, 3));
                JLabel id = new JLabel("    ID of Player");
                JLabel name = new JLabel("Name of Player");
                JLabel score = new JLabel("Score of Player  ");
                center.add(id);
                center.add(name);
                center.add(score);
                JLabel[] names = new JLabel[10];
                JLabel[] ids = new JLabel[10];
                JLabel[] scores = new JLabel[10];
                for (int i = 0; i < leaderboard.size(); i++) {
                    names[i] = new JLabel();
                    ids[i] = new JLabel();
                    scores[i] = new JLabel();
                    ids[i].setText("         " + leaderboard.get(i).getId() + "");
                    names[i].setText(leaderboard.get(i).getName());
                    scores[i].setText(leaderboard.get(i).getScore());
                    center.add(ids[i]);
                    center.add(names[i]);
                    center.add(scores[i]);
                }
                newP.add(center);
                newP.setSize(600, 500);
                newP.setVisible(true);
            }
        });

        JButton exit = new JButton("Exit to main Screen");
        exit.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                Menu();
            }
        });

        menuBtns = new javax.swing.JButton[2];
        menuBtns[0] = new javax.swing.JButton();
        menuBtns[1] = new javax.swing.JButton();
        menuBtns[0].setIcon(new ImageIcon(getClass().getResource("/Resources/smile.png")));
        menuBtns[0].setFocusPainted(true);
        menuBtns[0].setPreferredSize(new Dimension(28, 28));
        menuBtns[0].setBorderPainted(false);
        menuBtns[1].setText("Tutorial");
        menuBtns[1].addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                JFrame tutorial = new JFrame();
                JLabel t = new JLabel();
                t.setSize(600, 600);
                t.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Resources/tutorial.gif")));
                tutorial.add(t);
                tutorial.setSize(600, 600);
                tutorial.setVisible(true);
            }
        });
        menuBtns[0].addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    restartGame();
                } catch (UnsupportedAudioFileException ex) {
                    Logger.getLogger(manual.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(manual.class.getName()).log(Level.SEVERE, null, ex);
                } catch (LineUnavailableException ex) {
                    Logger.getLogger(manual.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        l1 = new JLabel();
        l1.setText(time + "");
        l1icon = new JLabel(new ImageIcon(getClass().getResource("/Resources/circular-clock.png")));
        l2 = new JLabel("10");
        l2icon = new JLabel(new ImageIcon(getClass().getResource("/Resources/bomb.png")));
        ImageIcon image = new ImageIcon(getClass().getResource("/Resources/tile.png"));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        info.add(l1icon);
        info.add(l1);
        info.add(l2icon);
        info.add(l2);

        Container c = frame.getContentPane();
        c.setLayout(new BorderLayout());

        menu.add(menuBtns[0]);
        menu.add(menuBtns[1]);
        menu.add(exit);
        c.add(menu, BorderLayout.NORTH);
        JPanel menu1 = new JPanel(new BorderLayout());
        menu1.add(cheat, BorderLayout.PAGE_START);
        menu1.add(leader, BorderLayout.PAGE_END);
        c.add(menu1, BorderLayout.WEST);

        for (int i = 0; i < 81; i++) {
            final int fNumber = i;
            button[i] = new JButton("");
            button[i].setIcon(image);
            button[i].setActionCommand(i + "");
            button[i].setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/Resources/tile_hover.png"))); // NOI18N
            button[i].addActionListener(this);
            button[i].addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (SwingUtilities.isRightMouseButton(e)) {
                        t.start();
                        fHelper(fNumber);
                    }
                }
            });
        }

        for (int i = 0; i < 81; i++) {
            grid.add(button[i]);
        }

        grid.setBorder(BorderFactory.createEmptyBorder(10, 50, 10, 50));

        c.add(grid, BorderLayout.CENTER);

        c.add(info, BorderLayout.SOUTH);

        frame.setSize(600, 500);
        frame.setVisible(true);
    }

    public void helper() {
        for (int i = 0; i < 81; i++) {
            if (i % 9 == 0) {
                System.out.print("\n");
            }
            System.out.print(matrix[i] + " ");

        }
    }

    public void fHelper(int i) {
        if (flagsPositions.contains(i) && chaypi[i] != true) {
            String soundFile = "D:/U N I/OOAD/Minesweeper/src/Sounds/unflag.wav";
            try {
                playSound(soundFile);

            } catch (UnsupportedAudioFileException ex) {
                Logger.getLogger(manual.class
                        .getName()).log(Level.SEVERE, null, ex);

            } catch (IOException ex) {
                Logger.getLogger(manual.class
                        .getName()).log(Level.SEVERE, null, ex);

            } catch (LineUnavailableException ex) {
                Logger.getLogger(manual.class
                        .getName()).log(Level.SEVERE, null, ex);
            }
            flagsPositions.remove(flagsPositions.indexOf(i));
            button[i].setIcon(new ImageIcon(getClass().getResource("/Resources/tile.png")));
            flags++;
            button[i].setRolloverEnabled(true);
        } else if (flags > 0 && chaypi[i] != true) {
            String soundFile = "D:/U N I/OOAD/Minesweeper/src/Sounds/flag.wav";
            try {
                playSound(soundFile);

            } catch (UnsupportedAudioFileException ex) {
                Logger.getLogger(manual.class
                        .getName()).log(Level.SEVERE, null, ex);

            } catch (IOException ex) {
                Logger.getLogger(manual.class
                        .getName()).log(Level.SEVERE, null, ex);

            } catch (LineUnavailableException ex) {
                Logger.getLogger(manual.class
                        .getName()).log(Level.SEVERE, null, ex);
            }
            flagsPositions.add(i);
            button[i].setIcon(new ImageIcon(getClass().getResource("/Resources/flag.png")));
            flags--;
            button[i].setRolloverEnabled(false);
        }
        l2.setText(flags + "");
        if (flags == 0) {
            status();
        }
    }

    public void poulateMatrix() {
        gameEnd = false;
        Random rand = new Random();
        for (int i = 0; i < 81; i++) {
            this.matrix[i] = -2; //-2 means not revealed
            this.chaypi[i] = false;
        }
        for (int i = 0; i < 10; i++) {
            this.matrix[i] = -1;
        }
        shuffleArray(matrix);
    }

    private static void shuffleArray(int[] array) {
        int index, temp;
        Random random = new Random();
        for (int i = array.length - 1; i > 0; i--) {
            index = random.nextInt(i + 1);
            temp = array[index];
            array[index] = array[i];
            array[i] = temp;
        }
    }

    public boolean reveal(int i) {
        turn++;
        int bCount = 0;
        if (turn == 1 && flagsPositions.contains(i)) {
            return false;
        }
        if (chaypi[i] != true) {
            if (this.matrix[i] != -1) {

                if (i % 9 != 0 && i + 8 < 81 && this.matrix[i + 8] == -1) {
                    bCount++;
                }
                if (i % 9 != 8 && i - 8 >= 0 && this.matrix[i - 8] == -1) {
                    bCount++;
                }
                if (i + 9 < 81 && this.matrix[i + 9] == -1) {
                    bCount++;
                }
                if (i - 9 >= 0 && this.matrix[i - 9] == -1) {
                    bCount++;
                }
                if (i % 9 != 8 && i + 10 < 81 && this.matrix[i + 10] == -1) {
                    bCount++;
                }
                if (i % 9 != 0 && i - 10 > 81 && this.matrix[i - 10] == -1) {
                    bCount++;
                }
                if (i % 9 != 8 && i + 1 < 81 && this.matrix[i + 1] == -1) {
                    bCount++;
                }
                if (i % 9 != 0 && i - 1 >= 0 && this.matrix[i - 1] == -1) {
                    bCount++;
                }
                this.matrix[i] = bCount;
                this.chaypi[i] = true;
                if (flagsPositions.contains(i)) {
                    flags++;
                    l2.setText(flags + "");
                }
            } else {
                if (turn == 1) {
                    gameEnd = true;
                }
                return false;
            }
            if (bCount == 0) {
                if (i % 9 != 0 && i + 8 < 81) {
                    reveal(i + 8);
                }
                if (i % 9 != 8 && i - 8 >= 0) {
                    reveal(i - 8);
                }
                if (i + 9 < 81) {
                    reveal(i + 9);
                }
                if (i - 9 >= 0) {
                    reveal(i - 9);
                }
                if (i % 9 != 8 && i + 10 < 81) {
                    reveal(i + 10);
                }
                if (i % 9 != 0 && i - 10 > 81) {
                    reveal(i - 10);
                }
                if (i % 9 != 8 && i + 1 < 81) {
                    reveal(i + 1);
                }
                if (i % 9 != 0 && i - 1 >= 0) {
                    reveal(i - 1);
                }
            }
        }
        return false;

    }

    public void uiButtons() {
        for (int i = 0; i < 81; i++) {
            if (matrix[i] == -1 && gameEnd == true) {
                button[i].setEnabled(false);
                button[i].setDisabledIcon(new javax.swing.ImageIcon(getClass().getResource("/Resources/bomb_mini.png")));
            } else if (matrix[i] == 0) {
                button[i].setEnabled(false);
                button[i].setDisabledIcon(new javax.swing.ImageIcon(getClass().getResource("/Resources/blank_tile.png")));
            } else if (matrix[i] == 1) {
                button[i].setEnabled(false);

                button[i].setDisabledIcon(new javax.swing.ImageIcon(getClass().getResource("/Resources/1.png")));
            } else if (matrix[i] == 2) {
                button[i].setEnabled(false);
                button[i].setDisabledIcon(new javax.swing.ImageIcon(getClass().getResource("/Resources/2.png")));
            } else if (matrix[i] == 3) {
                button[i].setEnabled(false);
                button[i].setDisabledIcon(new javax.swing.ImageIcon(getClass().getResource("/Resources/3.png")));
            } else if (matrix[i] == 4) {
                button[i].setEnabled(false);
                button[i].setDisabledIcon(new javax.swing.ImageIcon(getClass().getResource("/Resources/4.png")));
            }

        }

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        t.start();

        turn = 0;
        String soundFile = "D:/U N I/OOAD/Minesweeper/src/Sounds/click.wav";
        try {
            playSound(soundFile);

        } catch (UnsupportedAudioFileException ex) {
            Logger.getLogger(manual.class
                    .getName()).log(Level.SEVERE, null, ex);

        } catch (IOException ex) {
            Logger.getLogger(manual.class
                    .getName()).log(Level.SEVERE, null, ex);

        } catch (LineUnavailableException ex) {
            Logger.getLogger(manual.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        reveal(Integer.parseInt(e.getActionCommand()));
        uiButtons();
        if (remainingRevealed() == true) {
            status();
        }
        if (gameEnd == true) {
            t.stop();
            menuBtns[0].setIcon(new ImageIcon(getClass().getResource("/Resources/sed.png")));
            JFrame lost = new JFrame();
            JLabel t = new JLabel();
            t.setSize(600, 600);

            soundFile = "D:/U N I/OOAD/Minesweeper/src/Sounds/you_lose.wav";
            try {
                playSound(soundFile);

            } catch (UnsupportedAudioFileException ex) {
                Logger.getLogger(manual.class
                        .getName()).log(Level.SEVERE, null, ex);

            } catch (IOException ex) {
                Logger.getLogger(manual.class
                        .getName()).log(Level.SEVERE, null, ex);

            } catch (LineUnavailableException ex) {
                Logger.getLogger(manual.class
                        .getName()).log(Level.SEVERE, null, ex);
            }
            t.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Resources/lost.gif")));
            lost.add(t);
            lost.setSize(500, 500);
            lost.setVisible(true);
            db.insertLost(this.time + "", this.nameP.getText());
            String print = db.get(nameP.getText());
            int dialogButton = JOptionPane.YES_NO_OPTION;
            int dialogResult = JOptionPane.showConfirmDialog(null, "Game Over.\nTime play : " + time + " Seconds\n" + print + "Would You Like to Play Again?", "LOSER", dialogButton);
            if (dialogResult == JOptionPane.YES_OPTION) {
                lost.dispose();
                try {
                    restartGame();

                } catch (UnsupportedAudioFileException ex) {
                    Logger.getLogger(manual.class
                            .getName()).log(Level.SEVERE, null, ex);

                } catch (IOException ex) {
                    Logger.getLogger(manual.class
                            .getName()).log(Level.SEVERE, null, ex);

                } catch (LineUnavailableException ex) {
                    Logger.getLogger(manual.class
                            .getName()).log(Level.SEVERE, null, ex);
                }

            } else {
                frame.dispose();
                lost.dispose();
                Menu();
            }
        }

    }

    private void status() {
        if (popUp() == true) {
            t.stop();
            JFrame won = new JFrame();
            JLabel t = new JLabel();
            t.setSize(600, 600);
            t.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Resources/won.gif")));
            won.add(t);
            won.setSize(500, 500);
            won.setVisible(true);
            String soundFile = "D:/U N I/OOAD/Minesweeper/src/Sounds/oh_my_gah.wav";
            try {
                playSound(soundFile);

            } catch (UnsupportedAudioFileException ex) {
                Logger.getLogger(manual.class
                        .getName()).log(Level.SEVERE, null, ex);

            } catch (IOException ex) {
                Logger.getLogger(manual.class
                        .getName()).log(Level.SEVERE, null, ex);

            } catch (LineUnavailableException ex) {
                Logger.getLogger(manual.class
                        .getName()).log(Level.SEVERE, null, ex);
            }
            db.insertWon(this.time + "", this.nameP.getText());
            String print = db.get(nameP.getText());
            int dialogButton = JOptionPane.YES_NO_OPTION;
            int dialogResult = JOptionPane.showConfirmDialog(null, "Hell Yeah!\nTime play : " + time + " Seconds" + print + "\nYou won it. You wanna play again?", "WINNER", dialogButton);
            if (dialogResult == JOptionPane.YES_OPTION) {
                won.dispose();
                try {
                    restartGame();

                } catch (UnsupportedAudioFileException ex) {
                    Logger.getLogger(manual.class
                            .getName()).log(Level.SEVERE, null, ex);

                } catch (IOException ex) {
                    Logger.getLogger(manual.class
                            .getName()).log(Level.SEVERE, null, ex);

                } catch (LineUnavailableException ex) {
                    Logger.getLogger(manual.class
                            .getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                frame.dispose();
                won.dispose();
            }
        }
    }

    private boolean popUp() {
        for (int i = 0; i < flagsPositions.size(); i++) {
            if (matrix[flagsPositions.get(i)] != -1) {
                return false;
            }
        }
        return true;

    }

    private class Listener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            time++;
            l1.setText(time + "");
        }
    }

    private class startListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            starter++;
            if (starter > 3) {
                enter.setEnabled(true);
            }
        }
    }

    private void cheatReveal() {
        for (int i = 0; i < 81; i++) {
            if (matrix[i] == -1) {
                button[i].setIcon(new javax.swing.ImageIcon(getClass().getResource("/Resources/bomb_mini.png")));
                button[i].setRolloverEnabled(false);
            }
        }
    }

    void playSound(String soundFile) throws UnsupportedAudioFileException, MalformedURLException, IOException, LineUnavailableException {
        File f = new File(soundFile);
        AudioInputStream audioIn = AudioSystem.getAudioInputStream(f.toURI().toURL());
        Clip clip = AudioSystem.getClip();
        clip.open(audioIn);
        clip.start();
    }
}
