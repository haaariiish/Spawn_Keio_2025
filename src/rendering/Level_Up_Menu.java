package rendering;


import actions.ChangeGameState_toHome;
import actions.ChangeGameState_toLoading;
import core.Frame1;
import core.GameState;
import entities.Player;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;



public class Level_Up_Menu extends JPanel{
    private Frame1 mainFrame;
    private int virtual_add_attack=0;
    private int virtual_add_defense=0;
    private int virtual_add_hp=0;
    private double virtual_add_speed=0;
    private int virtual_level_add =0;
    private int score_cost = 5;
    private int scoreloss = 0;
    private int virtual_score =0;
    private Player player = null;
    
    // Labels pour afficher les valeurs des stats
    private JLabel healthLabel;
    private JLabel attackLabel;
    private JLabel defenseLabel;
    private JLabel speedLabel;
    private JLabel pointsLabel;
    private JLabel Level_Label;
    private JLabel CostLabel;
    
    // Boutons pour augmenter les stats
    private JButton healthButton;
    private JButton attackButton;
    private JButton defenseButton;
    private JButton speedButton;
    
    public Level_Up_Menu(Frame1 mainFrame1){
        mainFrame = mainFrame1;
        
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(new Color(40, 40, 60));
        
        initializeComponents();
    }
    
    private void initializeComponents() {
        // Title
        JLabel titleLabel = new JLabel("LEVEL UP !");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 32));
        titleLabel.setForeground(Color.YELLOW);
        titleLabel.setAlignmentX(CENTER_ALIGNMENT);
        titleLabel.setFocusable(false);
        add(Box.createVerticalStrut(30));
        add(titleLabel);
        add(Box.createVerticalStrut(20));
        
        // score
        pointsLabel = new JLabel("Score : 0");
        pointsLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        pointsLabel.setForeground(Color.WHITE);
        pointsLabel.setAlignmentX(CENTER_ALIGNMENT);
        pointsLabel.setFocusable(false);
        add(pointsLabel);
        Level_Label = new JLabel("Level : 0");
        Level_Label.setFont(new Font("Arial", Font.PLAIN, 18));
        Level_Label.setForeground(Color.WHITE);
        Level_Label.setAlignmentX(CENTER_ALIGNMENT);
        Level_Label.setFocusable(false);
        add(Level_Label);
        CostLabel = new JLabel("Cost : 0");
        CostLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        CostLabel.setForeground(Color.WHITE);
        CostLabel.setAlignmentX(CENTER_ALIGNMENT);
        CostLabel.setFocusable(false);
        add(CostLabel);
        add(Box.createVerticalStrut(30));
        
        // different stats
        add(createStatRow("Health", healthLabel = new JLabel("0"), healthButton = new JButton("+")));
        add(Box.createVerticalStrut(15));
        
        add(createStatRow("Attack", attackLabel = new JLabel("0"), attackButton = new JButton("+")));
        add(Box.createVerticalStrut(15));
        
        add(createStatRow("Defense", defenseLabel = new JLabel("0"), defenseButton = new JButton("+")));
        add(Box.createVerticalStrut(15));
        
        add(createStatRow("Speed", speedLabel = new JLabel("0"), speedButton = new JButton("+")));
        add(Box.createVerticalStrut(30));
        
        // Panel pour les boutons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
        buttonPanel.setBackground(new Color(40, 40, 60));
        buttonPanel.setMaximumSize(new Dimension(500, 60));
        buttonPanel.setFocusable(false);
        
        // Bouton de retour
        JButton resumeButton = new JButton("Return to Game (M)");
        resumeButton.setFont(new Font("Arial", Font.BOLD, 18));
        resumeButton.setPreferredSize(new Dimension(180, 50));
        resumeButton.setMaximumSize(new Dimension(180, 50));
        resumeButton.setBackground(new Color(200, 100, 100));
        resumeButton.setForeground(Color.BLACK);
        resumeButton.setFocusable(false);
        resumeButton.addActionListener(e -> {
            reset();
            mainFrame.getGame().changeGameState(GameState.PLAYING);
        });
        
        // confirmation button
        JButton confirmButton = new JButton("Confirm");
        confirmButton.setFont(new Font("Arial", Font.BOLD, 20));
        confirmButton.setPreferredSize(new Dimension(200, 50));
        confirmButton.setMaximumSize(new Dimension(200, 50));
        confirmButton.setFocusable(false);
        confirmButton.addActionListener(e -> onConfirmClicked());
        
        buttonPanel.add(Box.createHorizontalGlue());
        buttonPanel.add(resumeButton);
        buttonPanel.add(Box.createHorizontalStrut(20));
        buttonPanel.add(confirmButton);
        buttonPanel.add(Box.createHorizontalGlue());
        
        add(buttonPanel);

        
        // Action listeners avec restauration du focus
        healthButton.addActionListener(e -> {
            onStatButtonClickedInt("health",5);
            restoreFocus();
        });
        attackButton.addActionListener(e -> {
            onStatButtonClickedInt("attack",1);
            restoreFocus();
        });
        defenseButton.addActionListener(e -> {
            onStatButtonClickedInt("defense",1);
            restoreFocus();
        });
        speedButton.addActionListener(e -> {
            onStatButtonClickedDouble("speed",0.3);
            restoreFocus();
        });
    }
    
    // Restaurer le focus sur la frame principale
    private void restoreFocus() {
        SwingUtilities.invokeLater(() -> {
            mainFrame.requestFocus();
        });
    }
    
    private JPanel createStatRow(String statName, JLabel valueLabel, JButton plusButton) {
        JPanel rowPanel = new JPanel();
        rowPanel.setLayout(new BoxLayout(rowPanel, BoxLayout.X_AXIS));
        rowPanel.setBackground(new Color(50, 50, 70));
        rowPanel.setMaximumSize(new Dimension(500, 50));
        rowPanel.setFocusable(false);
        
        // Nom de la stat
        JLabel nameLabel = new JLabel(statName);
        nameLabel.setFont(new Font("Arial", Font.BOLD, 20));
        nameLabel.setForeground(Color.WHITE);
        nameLabel.setPreferredSize(new Dimension(150, 30));
        nameLabel.setFocusable(false);
        
        // Valeur de la stat
        valueLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        valueLabel.setForeground(Color.CYAN);
        valueLabel.setPreferredSize(new Dimension(100, 30));
        valueLabel.setFocusable(false);
        
        // Bouton plus
        plusButton.setFont(new Font("Arial", Font.BOLD, 24));
        plusButton.setPreferredSize(new Dimension(50, 40));
        plusButton.setBackground(new Color(100, 200, 100));
        plusButton.setForeground(Color.BLACK);
        plusButton.setFocusable(false);
        
        rowPanel.add(Box.createHorizontalStrut(50));
        rowPanel.add(nameLabel);
        rowPanel.add(Box.createHorizontalStrut(20));
        rowPanel.add(valueLabel);
        rowPanel.add(Box.createHorizontalGlue());
        rowPanel.add(plusButton);
        rowPanel.add(Box.createHorizontalStrut(50));
        
        return rowPanel;
    }
    
    // Méthode pour mettre à jour le player et rafraîchir l'affichage
    public void reset(){
        virtual_score = mainFrame.getGame().getGameWorld().getScore() ;
        virtual_add_attack=0;
        virtual_add_defense=0;
        virtual_add_hp=0;
        virtual_add_speed=0;
        virtual_level_add =0;
        
        scoreloss = 0;

        if(!(player==null)){
            score_cost = 5+3*player.getLevel();
        }
        updateDisplay();
    }
    
    public void setPlayer(Player player) {
        this.player = player;
        updateDisplay();
    }
    
    // Mise à jour de l'affichage des stats
    public void updateDisplay() {
        if (player != null) {
            
            healthLabel.setText(String.valueOf(player.getMaxHP()+virtual_add_hp));
            attackLabel.setText(String.valueOf(player.getAttack()+virtual_add_attack));
            defenseLabel.setText(String.valueOf(player.getDefense()+virtual_add_defense));
            speedLabel.setText(String.valueOf(player.getSpeed()+virtual_add_speed));
            Level_Label.setText(String.valueOf("Level: "+ (player.getLevel()+virtual_level_add)));
            pointsLabel.setText("Score: " + virtual_score);
            CostLabel.setText("Cost: " + (score_cost));
        }
        repaint();
    }
    
    // Méthode appelée quand on clique sur un bouton + (à personnaliser avec ta logique)
    private void onStatButtonClickedInt(String stat,int a) {
        System.out.println("Button click for: " + stat);
        if (virtual_score>=score_cost){
            if(stat.equals("health")){
            virtual_add_hp+=a;
            }
            if(stat.equals("attack")){
                virtual_add_attack+=a;
            }
            if(stat.equals("defense")){
                virtual_add_defense+=a;
            }
            virtual_score-=score_cost;
            score_cost+=3;
            virtual_level_add+= 1;
        }
        updateDisplay();
    }
    
    private void onStatButtonClickedDouble(String stat,double a) {
        System.out.println("Button click for: " + stat);
        if (virtual_score>=score_cost){
            if(stat.equals("speed")){
                virtual_add_speed += a;
            }
            virtual_score-=score_cost;
            score_cost+=3;
            virtual_level_add+= 1;
        }
        updateDisplay();
        
    }
    
    // Méthode appelée pour confirmer les changements
    private void onConfirmClicked() {
        System.out.println("Confirm ");
        if(player != null){
            player.setAttack(virtual_add_attack+player.getAttack());
            player.setDefense(virtual_add_defense+player.getDefense());
            player.setSpeed(virtual_add_speed+player.getSpeed());
            player.setMaxHp(virtual_add_hp+player.getMaxHP());
            player.LevelUp(virtual_level_add);
            mainFrame.getGame().getGameWorld().setScore(virtual_score);
        }
        reset();
        updateDisplay();
        //mainFrame.getGame().changeGameState(GameState.PLAYING);
    }
}