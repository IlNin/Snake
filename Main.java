import javax.swing.*;

public class Main {
 public static void main(String[] args) {
  Menu menu = new Menu(0, 0); // Apre il menu di gioco (gli zeri stanno per i punteggi record)
  menu.setSize(510,290); // Regola le dimensioni della finestra
  menu.setResizable(false); // La finestra non puo' essere ridimensionata
  menu.setVisible(true); // La finestra e' visibile
  menu.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  } } // Chiude automaticamente il programma se si chiude la finestra