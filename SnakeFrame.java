import javax.swing.*;
import java.util.*;
import java.awt.*;
import java.awt.*;
import java.awt.event.*;
import java.beans.*;

public class SnakeFrame extends JFrame {
 JPanel pannello; // Pannello principale a cui verranno aggiunti tutti gli altri componenti
 JPanel p; // Panello dove verra' mostrata la griglia del gioco
 JLabel punteggio; // Contiene il punteggio attuale del giocatore
 JLabel record; // Contiene il punteggio record
 int contatore = 0; // Numero di frutti mangiati
 int contatoreRecord; // Record di frutti mangiati
 JLabel[][] griglia; // Griglia di gioco
 Snake snake; // Serpente iniziale
 char input = 'd'; // Indica la direzione dello spostamento del serpente
 boolean mosso = false; // Diventa true quando il serpente si muove per la prima volta. Fondamentale per il timer!
 java.util.Timer timer; // Timer che permette lo spostamento del serpente
 boolean pausa = false; // Indica se il gioco e' in pausa
 int overheadMenu; // Informazioni importanti per il menu di gioco
 int velocita; // 1 se il gioco è lento, 2 se è normale, 3 se è veloce
 String nomeVelocita; // Indica la velocità del gioco sottoforma di parola
 
 public SnakeFrame(int contatoreRecord, int overheadMenu, int velocita)  {
  this.contatoreRecord = contatoreRecord; // Inizializza il record
  this.overheadMenu = overheadMenu; // Overhead contiene il record dell'IA nella modalita' "Test IA"
  this.velocita = velocita; // Inizializza la velocita' scelta dal giocatore nel menu
  if (velocita == 1) {
   nomeVelocita = "Lento"; }
  else if (velocita == 2) {
   nomeVelocita = "Normale"; }
  else {
   nomeVelocita = "Veloce"; }
  this.setTitle("SNAKE - Classica " + nomeVelocita + " - In Gioco"); // Titolo che appare in alto alla finestra
  setFocusable(true); // Essere "focusable" permette di ricevere input
  requestFocus();
  
  pannello = new JPanel(); // Crea il pannello principale a cui tutti i componenti si attaccheranno. In particolare un altro pannello e due JLabels
  pannello.setLayout(new BoxLayout(pannello, BoxLayout.PAGE_AXIS)); // Il pannello aggiungerà gli altri componenti a colonna
  this.setContentPane(pannello);
  
  p = new JPanel(); // Crea il pannello dove viene visualizzata la griglia di gioco
  p.setBackground(Color.BLACK); // Lo sfondo e' nero per distinguerlo dalle caselle bianche
  p.setVisible(true);
  pannello.add(p);
  
  punteggio = new JLabel("Il tuo punteggio: " + contatore); // Crea la JLabel dove viene visualizzato il punteggio attuale
  punteggio.setAlignmentX(Component.CENTER_ALIGNMENT); // Piazza la label al centro della riga
  pannello.add(punteggio);
  
  record = new JLabel("Il tuo record: " + contatoreRecord); // Crea la JLabel dove viene visualizzato il record attuale
  record.setAlignmentX(Component.CENTER_ALIGNMENT);
  pannello.add(record);
  
  creaGriglia(); // Prepara la griglia di gioco
  snake = new Snake();
  inserisciFrutto();
  
  this.addKeyListener(new KeyAdapter() { // Permette di accettare i comandi della tastiera come input
   public void keyTyped(KeyEvent e) {;
    if ((e.getKeyChar() == 'w' && snake.getElementi().get(0).getX() != snake.getElementi().get(1).getX()+1) || (e.getKeyChar() == 's' && snake.getElementi().get(0).getX() != snake.getElementi().get(1).getX()-1) || (e.getKeyChar() == 'a' && snake.getElementi().get(0).getY() != snake.getElementi().get(1).getY()+1) || (e.getKeyChar() == 'd' && snake.getElementi().get(0).getY() != snake.getElementi().get(1).getY()-1) && (pausa == false)) {
     input = e.getKeyChar(); // Con l'if precedente si controlla se l'input e' valido (ovvero se la testa del serpente non si scontra con il secondo elemento) 
     if (mosso == false) {
      mosso = true;
      settaTimer(); } }
    else if (e.getKeyChar() == 'p' && pausa == false && mosso == true) { // Mette il gioco in pausa
     timer.cancel();
     setTitle("SNAKE - Classica " + nomeVelocita + " - In Pausa");
     pausa = true; }
    else if (e.getKeyChar() == 'e' && pausa == true) { // Torna al menu dalla pausa
     apriMenu();  }
    else if (e.getKeyChar() == 'p' && pausa == true) { // Fa uscire il gioco dalla pausa
     setTitle("SNAKE - Classica " + nomeVelocita + " - In Gioco");
     pausa = false;
     settaTimer(); } } }); }
  
 class gioco extends TimerTask { // Al termine del timer, il serpente viene mosso
  public void run() {
   boolean esito = muoviSnake(input);
   if (esito == false) { // Il serpente si scontra contro qualcosa e il gioco termina
    timer.cancel();
    griglia[snake.getElementi().get(0).getX()][snake.getElementi().get(0).getY()].setBackground(Color.BLACK); // Segnala il punto d'impatto
    gameOver();	} } } // Il timer viene terminato

 public void creaGriglia() { // Prepara la griglia quando il gioco viene avviato
  griglia = new JLabel[11][21];
  for (int i = 0; i < 11; i++) {
   for (int j = 0; j < 21; j++) {
    griglia[i][j] = new JLabel("     "); // Le caselle sono costituite da delle JLabel vuote colorate di bianco
    griglia[i][j].setBackground(Color.WHITE);
    griglia[i][j].setOpaque(true);
    p.add(griglia[i][j]); } }
  for (int j = 0; j < 7; j++) {
   griglia[10][j].setBackground(Color.BLUE); } } // Le caselle iniziale occupate dal serpente sono invece blu
 
 public void inserisciFrutto() { // Inserisce il frutto in una posizione casuale
  boolean flag = false;
  while (flag == false) {
   Random randX = new Random();
   Random randY = new Random();
   int X = randX.nextInt(11);
   int Y = randY.nextInt(21);
   if (griglia[X][Y].getBackground() == Color.WHITE) { // Ovviamente bisogna controllare se la posizione non sia gia' occupata dal serpente!
    griglia[X][Y].setBackground(Color.RED);
    flag = true; } } }
 
 public boolean posizioneCorretta(int x, int y) { // Controlla se la nuova posizione della testa del serpente è corretta! (Metodo ausiliario di "muoviSnake")
  try {
   if (griglia[x][y].getBackground() == Color.BLUE) {
    return false; }
   else {
    return true; } }
  catch(ArrayIndexOutOfBoundsException e) {
   return false; } }
 
 public boolean muoviSnake(char input) { // Muove il serpente a seconda della posizione scelta. Ritorna true se il movimento è possibile. 
  int aggiungiX = 0;
  int aggiungiY = 0;
  if (input == 'w') { // Calcola di quanto si deve muovere la testa a seconda dell'input scelto
   aggiungiX = -1; }
  else if (input == 's') {
   aggiungiX = 1; }
  else if (input == 'd') {
   aggiungiY = 1; }
  else if (input == 'a') {
   aggiungiY = -1; }
  
  Iterator it = snake.getElementi().iterator();
  boolean testa = true; // Controlla se attualmente si sta muovendo la testa del serpente
  boolean mangiato = false; // Controlla se il serpente ha mangiato
  int ultimoX = 0; // Ultimo X ad essere stato mosso
  int ultimoY = 0; // Utimo Y ad essere stato mosso
  int swapX = 0;
  int swapY = 0;
  
  while (it.hasNext()) { // Scorre gli elementi del serpente
   Coordinate correnti = (Coordinate) it.next();
   if (testa == true) { // Stiamo muovendo l'elemento di testa
    ultimoX = correnti.getX(); // Salva le coordinate attuali della testa
    ultimoY = correnti.getY();
    if (posizioneCorretta(ultimoX + aggiungiX, ultimoY + aggiungiY) == false) { // Se il serpente si scontra contro qualcosa il gioco finisce
     return false; } 
    correnti.setX(ultimoX+aggiungiX);  // La testa si sposta e assume nuove coordinate
    correnti.setY(ultimoY+aggiungiY);
    if (griglia[correnti.getX()][correnti.getY()].getBackground() == Color.RED) { // Il serpente mangia, viene aggiornato il punteggio e un altro frutto viene aggiunto
     inserisciFrutto();
     aggiornaPunteggio();
     mangiato = true; }
    griglia[correnti.getX()][correnti.getY()].setBackground(Color.BLUE);
    testa = false; }
   else { // Stiamo muovendo un elemento interno al serpente, che imita il movimento di quello successivo
    swapX = correnti.getX();
    swapY = correnti.getY();
    correnti.setX(ultimoX);
    correnti.setY(ultimoY);
    griglia[ultimoX][ultimoY].setBackground(Color.BLUE);
    ultimoX = swapX;
    ultimoY = swapY; } }
	
  if ( mangiato == true) { // Se il serpente ha mangiato, viene aggiunto un nuovo elemento in coda
   snake.getElementi().add(new Coordinate(swapX, swapY));
   griglia[swapX][swapY].setBackground(Color.BLUE); }
  else {
   griglia[swapX][swapY].setBackground(Color.WHITE); }
  return true;  }
  
 public void aggiornaPunteggio() { // Aggiorna il punteggio attuale
  contatore = contatore + velocita; // Il punteggio dopo aver mangiato un frutto dipende dalla velocita' scelta
  if (contatore > contatoreRecord) {
   contatoreRecord = contatore; }
  punteggio.setText("Il tuo punteggio: " + contatore);
  record.setText("Il tuo record: " + contatoreRecord); }
 
 public void gameOver() { // Chiede al giocatore se tornare al menu o iniziare una nuova partita
  String[] opzioni = {"Nuova Partita", "Menu"};
  int scelta = JOptionPane.showOptionDialog(null, new JLabel("Game Over!", JLabel.CENTER), "SNAKE - Classica " + nomeVelocita, JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, opzioni, opzioni[0]);
  if (scelta == 0) {
   inizioNuovaPartita(); }
  else {
   apriMenu();  } }
 
 public void settaTimer() {  // Il timer permette lo spostamento automatico del serpente, e regola la sua velocità 
  timer = new java.util.Timer();
  if (velocita == 1) {
   timer.schedule(new gioco(), 0, 150); }
  else if (velocita == 2) {
   timer.schedule(new gioco(), 0, 100); }
  else {
   timer.schedule(new gioco(), 0, 50); } }
 
 public void apriMenu() { // Torna al menu
  Menu menu = new Menu(contatoreRecord, overheadMenu);
  menu.setSize(510,290);
  menu.setVisible(true);
  menu.setResizable(false);
  menu.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
  setVisible(false);
  dispose();  }
 
 public void inizioNuovaPartita() { // Crea una nuova partita
  SnakeFrame nuovaPartita = new SnakeFrame(contatoreRecord, overheadMenu, velocita);
  nuovaPartita.setSize(435, 296);
  nuovaPartita.setResizable(false);
  nuovaPartita.setVisible(true);
  nuovaPartita.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
  setVisible(false);
  dispose(); } }
