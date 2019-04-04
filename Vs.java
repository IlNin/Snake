import javax.swing.*;
import java.util.*;
import java.awt.*;
import java.awt.*;
import java.awt.event.*;
import java.beans.*;

// Per commenti piu' dettagliati controllare SnakeFrame.java e IASnake.java dato che gran parte del codice e' in comune
public class Vs extends JFrame {
 JPanel pannello;
 JPanel p;
 JPanel p1;
 JPanel p2;
 
 int contatoreGiocatore1 = 0; // Numero di frutti mangiati dal giocatore 1
 int contatoreGiocatore2 = 0; // Numero di frutti mangiati dal giocatore 2
 JLabel nomeGiocatore1;
 JLabel[][] grigliaGiocatore1; 
 JLabel nomeGiocatore2;
 JLabel[][] grigliaGiocatore2;
 Snake snakeGiocatore1;
 Snake snakeGiocatore2;
 char inputGiocatore1 = 'd'; 
 int inputGiocatore2;
 boolean mosso = false;
 java.util.Timer timerGiocatore1; // Timer che permette lo spostamento del serpente
 java.util.Timer timerGiocatore2;
 boolean pausa = false; // Indica se il gioco e' in pausa
 boolean modalita; // Indica se si gioca contro un altro umano o la CPU
 boolean giocatore1GameOver = false;
 boolean giocatore2GameOver = false;
 
 int posizioneFruttoX; // Coordinata X del frutto
 int posizioneFruttoY; // Coordinata Y del frutto
 int posizioneTestaX = 10; // Coordinata X della testa
 int posizioneTestaY = 6; // Coordinata Y della testa
 boolean swap = true; // Indica il tipo di IA che la CPU deve utilizzare
 int contatoreSwap = 0; // Quando raggiunge una certa cifra parte lo swap
 
 int overheadMenu1;
 int overheadMenu2;
 
 public Vs(int overheadMenu1, int overheadMenu2, boolean modalita)  {
  if (modalita == true) {
  inputGiocatore2 = KeyEvent.VK_RIGHT; }
  else {
   inputGiocatore2 = 'd'; }
  this.overheadMenu1 = overheadMenu1;
  this.overheadMenu2 = overheadMenu2;
  this.modalita = modalita;
  if (modalita == true) {
   this.setTitle("SNAKE - Player VS Player - In Gioco"); }
  else {
   this.setTitle("SNAKE - Player VS CPU - In Gioco"); }
  setFocusable(true);
  requestFocus();
  
  pannello = new JPanel(); // Crea il pannello principale a cui tutti i componenti si attaccheranno. In particolare un altro pannello e due JLabels
  pannello.setLayout(new BoxLayout(pannello, BoxLayout.PAGE_AXIS));
  this.setContentPane(pannello);
  
  nomeGiocatore1 = new JLabel("Griglia del Giocatore 1                                      Punteggio: " + contatoreGiocatore1);
  nomeGiocatore1.setAlignmentX(Component.CENTER_ALIGNMENT);
  pannello.add(nomeGiocatore1);
  
  p1 = new JPanel(); // Crea il pannello dove viene visualizzato il gioco del primo giocatore
  p1.setBackground(Color.BLACK);
  p1.setVisible(true);
  pannello.add(p1);
  
  if (modalita == true) {
   nomeGiocatore2 = new JLabel("Griglia del Giocatore 2                                   Punteggio: " + contatoreGiocatore2); }
  else {
   nomeGiocatore2 = new JLabel("Griglia del Computer                                 Punteggio: " + contatoreGiocatore2); }
  nomeGiocatore2.setAlignmentX(Component.CENTER_ALIGNMENT);
  pannello.add(nomeGiocatore2);
  
  p2 = new JPanel();
  p2.setBackground(Color.BLACK);
  p2.setVisible(true);
  pannello.add(p2);


  creaGrigliaGiocatore1(); // Prepara il gioco
  creaGrigliaGiocatore2();
  snakeGiocatore1 = new Snake();
  snakeGiocatore2 = new Snake();
  inserisciFruttoGiocatore1();
  inserisciFruttoGiocatore2();
  
  this.addKeyListener(new KeyAdapter() { // Permette di accettare i comandi della tastiera come input
   public void keyPressed(KeyEvent e) {
    if (modalita == true && ((e.getKeyCode() == KeyEvent.VK_UP && snakeGiocatore2.getElementi().get(0).getX() != snakeGiocatore2.getElementi().get(1).getX()+1) || (e.getKeyCode() == KeyEvent.VK_DOWN && snakeGiocatore2.getElementi().get(0).getX() != snakeGiocatore2.getElementi().get(1).getX()-1) || (e.getKeyCode() == KeyEvent.VK_LEFT && snakeGiocatore2.getElementi().get(0).getY() != snakeGiocatore2.getElementi().get(1).getY()+1) || (e.getKeyCode() == KeyEvent.VK_RIGHT && snakeGiocatore2.getElementi().get(0).getY() != snakeGiocatore2.getElementi().get(1).getY()-1) && (pausa == false))) {
     inputGiocatore2 = e.getKeyCode();
     if (mosso == false) {
      mosso = true;
      settaTimer(); } } }
   public void keyTyped(KeyEvent e) {
    if ((e.getKeyChar() == 'w' && snakeGiocatore1.getElementi().get(0).getX() != snakeGiocatore1.getElementi().get(1).getX()+1) || (e.getKeyChar() == 's' && snakeGiocatore1.getElementi().get(0).getX() != snakeGiocatore1.getElementi().get(1).getX()-1) || (e.getKeyChar() == 'a' && snakeGiocatore1.getElementi().get(0).getY() != snakeGiocatore1.getElementi().get(1).getY()+1) || (e.getKeyChar() == 'd' && snakeGiocatore1.getElementi().get(0).getY() != snakeGiocatore1.getElementi().get(1).getY()-1) && (pausa == false)) {
     inputGiocatore1 = e.getKeyChar();
     if (mosso == false) {
      mosso = true;
      settaTimer(); } }
    else if (e.getKeyChar() == 'p' && pausa == false && mosso == true) { // Mette il gioco in pausa
     if (giocatore1GameOver == false) {
      timerGiocatore1.cancel(); }
     if (giocatore2GameOver == false) {
      timerGiocatore2.cancel(); }
     if (modalita == false) {
      setTitle("SNAKE - Player VS CPU - In Pausa"); }
     else {
      setTitle("SNAKE - Player VS Player - In Pausa"); }
     pausa = true; }
    else if (e.getKeyChar() == 'e' && pausa == true) {
     apriMenu();  }
    else if (e.getKeyChar() == 'p' && pausa == true) { // Fa uscire il gioco dalla pausa
     if (modalita == false) {
      setTitle("SNAKE - Player VS CPU - In Gioco"); }
     else {
      setTitle("SNAKE - Player VS Player - In Gioco"); }
     pausa = false;
     settaTimer(); } } }); }
  
 class giocoGiocatore1 extends TimerTask { 
  public void run() {
   if (giocatore2GameOver == true && contatoreGiocatore1 > contatoreGiocatore2) {
    timerGiocatore1.cancel();
    gameOver(); }
   else {
    boolean esito = muoviSnakeGiocatore1();
    if (esito == false) {
     timerGiocatore1.cancel();
     grigliaGiocatore1[snakeGiocatore1.getElementi().get(0).getX()][snakeGiocatore1.getElementi().get(0).getY()].setBackground(Color.BLACK);
     giocatore1GameOver = true;
     if (giocatore2GameOver == true) {
      gameOver(); } } } } }
 
 class giocoGiocatore2 extends TimerTask {
  public void run() {
   if (giocatore1GameOver == true && contatoreGiocatore2 > contatoreGiocatore1) {
    timerGiocatore2.cancel();
    gameOver(); }
   else {
    if (modalita == false) {
     scegliInput(); }
    boolean esito = muoviSnakeGiocatore2();
    if (esito == false) {
     timerGiocatore2.cancel();
     grigliaGiocatore2[posizioneTestaX][posizioneTestaY].setBackground(Color.BLACK);
     giocatore2GameOver = true;
     if (giocatore1GameOver == true) {
      gameOver(); } } } } }

 public void creaGrigliaGiocatore1() { // Prepara la griglia del giocatore 1 quando il gioco viene avviato
  grigliaGiocatore1 = new JLabel[11][21];
  for (int i = 0; i < 11; i++) {
   for (int j = 0; j < 21; j++) {
    grigliaGiocatore1[i][j] = new JLabel("     "); // Le caselle sono costituite da delle JLabel vuote colorate di bianco
    grigliaGiocatore1[i][j].setBackground(Color.WHITE);
    grigliaGiocatore1[i][j].setOpaque(true);
    p1.add(grigliaGiocatore1[i][j]); } }
  for (int j = 0; j < 7; j++) {
   grigliaGiocatore1[10][j].setBackground(Color.BLUE); } } // Le caselle iniziale occupate dal serpente sono invece blu
 
 public void creaGrigliaGiocatore2() { // Prepara la griglia del giocatore 2 quando il gioco viene avviato
  grigliaGiocatore2 = new JLabel[11][21];
  for (int i = 0; i < 11; i++) {
   for (int j = 0; j < 21; j++) {
    grigliaGiocatore2[i][j] = new JLabel("     "); // Le caselle sono costituite da delle JLabel vuote colorate di bianco
    grigliaGiocatore2[i][j].setBackground(Color.WHITE);
    grigliaGiocatore2[i][j].setOpaque(true);
    p2.add(grigliaGiocatore2[i][j]); } }
  for (int j = 0; j < 7; j++) {
   grigliaGiocatore2[10][j].setBackground(Color.BLUE); } } // Le caselle iniziale occupate dal serpente sono invece blu
 
 public void inserisciFruttoGiocatore1() { // Inserisce il frutto in una posizione casuale
  boolean flag = false;
  while (flag == false) {
   Random randX = new Random();
   Random randY = new Random();
   int X = randX.nextInt(11);
   int Y = randY.nextInt(21);
   if (grigliaGiocatore1[X][Y].getBackground() == Color.WHITE) {
    grigliaGiocatore1[X][Y].setBackground(Color.RED);
    flag = true; } } }
  
 public void inserisciFruttoGiocatore2() { // Inserisce il frutto in una posizione casuale
  boolean flag = false;
  while (flag == false) {
   Random randX = new Random();
   Random randY = new Random();
   int X = randX.nextInt(11);
   int Y = randY.nextInt(21);
   if (grigliaGiocatore2[X][Y].getBackground() == Color.WHITE) {
    posizioneFruttoX = X;
    posizioneFruttoY = Y;
    grigliaGiocatore2[X][Y].setBackground(Color.RED);
    flag = true; } } }
 
 public boolean posizioneCorrettaGiocatore1(int x, int y) { // Controlla se la nuova posizione della testa del serpente è corretta! (Metodo ausiliario di "muoviSnake")
  try {
   if (grigliaGiocatore1[x][y].getBackground() == Color.BLUE) {
    return false; }
   else {
    return true; } }
  catch(ArrayIndexOutOfBoundsException e) {
   return false; } }
 
 public boolean posizioneCorrettaGiocatore2(int x, int y) { // Controlla se la nuova posizione della testa del serpente è corretta! (Metodo ausiliario di "muoviSnake")
  try {
   if (grigliaGiocatore2[x][y].getBackground() == Color.BLUE) {
    return false; }
   else {
    return true; } }
  catch(ArrayIndexOutOfBoundsException e) {
   return false; } }
 
 public boolean muoviSnakeGiocatore1() { // Muove il serpente a seconda della posizione scelta. Ritorna true se il movimento è possibile. 
  int aggiungiX = 0;
  int aggiungiY = 0;
  if (inputGiocatore1 == 'w') { // Calcola di quanto si deve muovere la testa a seconda dell'input scelto
   aggiungiX = -1; }
  else if (inputGiocatore1 == 's') {
   aggiungiX = 1; }
  else if (inputGiocatore1 == 'd') {
   aggiungiY = 1; }
  else if (inputGiocatore1 == 'a') {
   aggiungiY = -1; }
  
  Iterator it = snakeGiocatore1.getElementi().iterator();
  boolean testa = true; // Controlla se ora si sta muovendo la testa del serpente
  boolean mangiato = false; // Controlla se il serpente ha mangiato
  int ultimoX = 0; // Ultimo X ad essere stato mosso
  int ultimoY = 0; // Ultimo Y ad essere stato mosso
  int swapX = 0;
  int swapY = 0;
  
  while (it.hasNext()) { // Scorre gli elementi del serpente
   Coordinate correnti = (Coordinate) it.next();
   if (testa == true) { // Stiamo muovendo l'elemento di testa
    ultimoX = correnti.getX(); // Salva le coordinate attuali della testa
    ultimoY = correnti.getY();
    if (posizioneCorrettaGiocatore1(ultimoX + aggiungiX, ultimoY + aggiungiY) == false) { // Se il serpente si scontra contro qualcosa il gioco finisce
     return false; } 
    correnti.setX(ultimoX+aggiungiX);  // La testa si sposta e assume nuove coordinate
    correnti.setY(ultimoY+aggiungiY);
    if (grigliaGiocatore1[correnti.getX()][correnti.getY()].getBackground() == Color.RED) { // Il serpente mangia, viene aggiornato il punteggio e un altro frutto viene aggiunto
     inserisciFruttoGiocatore1();
     aggiornaPunteggioGiocatore1();
     mangiato = true; }
    grigliaGiocatore1[correnti.getX()][correnti.getY()].setBackground(Color.BLUE);
    testa = false;	}
   else { // Stiamo muovendo un elemento interno al serpente, che imita il movimento di quello successivo
    swapX = correnti.getX();
    swapY = correnti.getY();
    correnti.setX(ultimoX);
    correnti.setY(ultimoY);
    grigliaGiocatore1[ultimoX][ultimoY].setBackground(Color.BLUE);
    ultimoX = swapX;
    ultimoY = swapY; } }
	
  if (mangiato == true) { // Se il serpente ha mangiato, viene aggiunto un nuovo elemento in coda
   snakeGiocatore1.getElementi().add(new Coordinate(swapX, swapY));
   grigliaGiocatore1[swapX][swapY].setBackground(Color.BLUE); }
  else {
   grigliaGiocatore1[swapX][swapY].setBackground(Color.WHITE); }
  return true;  }
 
 public boolean muoviSnakeGiocatore2() { // Muove il serpente a seconda della posizione scelta. Ritorna true se il movimento è possibile. 
  int aggiungiX = 0;
  int aggiungiY = 0;
  if (modalita == true) {
   if (inputGiocatore2 == KeyEvent.VK_UP) { // Calcola di quanto si deve muovere la testa a seconda dell'input scelto
    aggiungiX = -1; }
   else if (inputGiocatore2 == KeyEvent.VK_DOWN) {
    aggiungiX = 1; }
   else if (inputGiocatore2 == KeyEvent.VK_RIGHT) {
    aggiungiY = 1; }
   else if (inputGiocatore2 == KeyEvent.VK_LEFT) {
    aggiungiY = -1; } }
  else {
   if (inputGiocatore2 == 'w') { 
    aggiungiX = -1; }
   else if (inputGiocatore2 == 's') {
    aggiungiX = 1; }
   else if (inputGiocatore2 == 'd') {
    aggiungiY = 1; }
   else if (inputGiocatore2 == 'a') {
    aggiungiY = -1; } }
  
  Iterator it = snakeGiocatore2.getElementi().iterator();
  boolean testa = true; // Controlla se ora si sta muovendo la testa del serpente
  boolean mangiato = false; // Controlla se il serpente ha mangiato
  int ultimoX = 0; // Ultimo X ad essere stato mosso
  int ultimoY = 0; // Ultimo Y ad essere stato mosso
  int swapX = 0;
  int swapY = 0;
  
  while (it.hasNext()) { // Scorre gli elementi del serpente
   Coordinate correnti = (Coordinate) it.next();
   if (testa == true) { // Stiamo muovendo l'elemento di testa
    ultimoX = correnti.getX(); // Salva le coordinate attuali della testa
    ultimoY = correnti.getY();
    if (posizioneCorrettaGiocatore2(ultimoX + aggiungiX, ultimoY + aggiungiY) == false) { // Se il serpente si scontra contro qualcosa il gioco finisce
     return false; } 
    correnti.setX(ultimoX+aggiungiX);  // La testa si sposta e assume nuove coordinate
    correnti.setY(ultimoY+aggiungiY);
    if (grigliaGiocatore2[correnti.getX()][correnti.getY()].getBackground() == Color.RED) { // Il serpente mangia, viene aggiornato il punteggio e un altro frutto viene aggiunto
     inserisciFruttoGiocatore2();
     aggiornaPunteggioGiocatore2();
     mangiato = true; }
    grigliaGiocatore2[correnti.getX()][correnti.getY()].setBackground(Color.BLUE);
    posizioneTestaX = correnti.getX();
    posizioneTestaY = correnti.getY();
    testa = false;	}
   else { // Stiamo muovendo un elemento interno al serpente, che imita il movimento di quello successivo
    swapX = correnti.getX();
    swapY = correnti.getY();
    correnti.setX(ultimoX);
    correnti.setY(ultimoY);
    grigliaGiocatore2[ultimoX][ultimoY].setBackground(Color.BLUE);
    ultimoX = swapX;
    ultimoY = swapY; } }
	
  if (mangiato == true) { // Se il serpente ha mangiato, viene aggiunto un nuovo elemento in coda
   snakeGiocatore2.getElementi().add(new Coordinate(swapX, swapY));
   grigliaGiocatore2[swapX][swapY].setBackground(Color.BLUE); }
  else {
   grigliaGiocatore2[swapX][swapY].setBackground(Color.WHITE); }
  return true;  }
 
 public void scegliInput() {
  int inputW = calcolaBlocchiBianchi('w');
  int inputD = calcolaBlocchiBianchi('d');
  int inputS = calcolaBlocchiBianchi('s');
  int inputA = calcolaBlocchiBianchi('a');
  if (swap == true && contatoreGiocatore2 > 3) { // Tipo AI 1
   if (posizioneFruttoY > posizioneTestaY) { // Calcola l'input se il frutto si trova piu' a destra del serpente
    if (posizioneTestaY != 20 && grigliaGiocatore2[posizioneTestaX][posizioneTestaY+1].getBackground() != Color.BLUE && inputD >= inputW && inputD >= inputS) {
     inputGiocatore2 = 'd'; }
    else if (posizioneTestaX != 0 && grigliaGiocatore2[posizioneTestaX-1][posizioneTestaY].getBackground() != Color.BLUE && inputW >= inputS) {
     inputGiocatore2 = 'w'; }
    else if (posizioneTestaX != 10 && grigliaGiocatore2[posizioneTestaX+1][posizioneTestaY].getBackground() != Color.BLUE && inputW <= inputS) {
     inputGiocatore2 = 's'; }
    else if (posizioneTestaY != 0 && grigliaGiocatore2[posizioneTestaX][posizioneTestaY-1].getBackground() != Color.BLUE) {
     inputGiocatore2 = 'a'; }
    else if (posizioneTestaY != 20 && grigliaGiocatore2[posizioneTestaX][posizioneTestaY+1].getBackground() != Color.BLUE) {
     inputGiocatore2 = 'd'; }
    else if (posizioneTestaX != 0 && grigliaGiocatore2[posizioneTestaX-1][posizioneTestaY].getBackground() != Color.BLUE && posizioneFruttoX < posizioneTestaX) {
     inputGiocatore2 = 'w'; }
    else if (posizioneTestaX != 10 && grigliaGiocatore2[posizioneTestaX+1][posizioneTestaY].getBackground() != Color.BLUE && posizioneFruttoX > posizioneTestaX) {
     inputGiocatore2 = 's'; }
    else if (posizioneTestaX != 0 && grigliaGiocatore2[posizioneTestaX-1][posizioneTestaY].getBackground() != Color.BLUE) {
     inputGiocatore2 = 'w'; }
    else if (posizioneTestaX != 10 && grigliaGiocatore2[posizioneTestaX+1][posizioneTestaY].getBackground() != Color.BLUE) {
     inputGiocatore2 = 's'; }
    else {
     inputGiocatore2 = 'd'; } }
	 
   else if (posizioneFruttoY < posizioneTestaY) { // Calcola l'input se il frutto si trova piu' a sinistra del serpente
    if (posizioneTestaY != 0 && grigliaGiocatore2[posizioneTestaX][posizioneTestaY-1].getBackground() != Color.BLUE && inputA >= inputW && inputA >= inputS) {
     inputGiocatore2 = 'a'; }
    else if (posizioneTestaX != 0 && grigliaGiocatore2[posizioneTestaX-1][posizioneTestaY].getBackground() != Color.BLUE && inputW >= inputS) {
     inputGiocatore2 = 'w'; }
    else if (posizioneTestaX != 10 && grigliaGiocatore2[posizioneTestaX+1][posizioneTestaY].getBackground() != Color.BLUE && inputW <= inputS) {
     inputGiocatore2 = 's'; }
    else if (posizioneTestaY != 20 && grigliaGiocatore2[posizioneTestaX][posizioneTestaY+1].getBackground() != Color.BLUE) {
     inputGiocatore2 = 'd'; }
    else if (posizioneTestaY != 0 && grigliaGiocatore2[posizioneTestaX][posizioneTestaY-1].getBackground() != Color.BLUE) {
     inputGiocatore2 = 'a'; }
    else if (posizioneTestaX != 0 && grigliaGiocatore2[posizioneTestaX-1][posizioneTestaY].getBackground() != Color.BLUE && posizioneFruttoX < posizioneTestaX) {
     inputGiocatore2 = 'w'; }
    else if (posizioneTestaX != 10 && grigliaGiocatore2[posizioneTestaX+1][posizioneTestaY].getBackground() != Color.BLUE && posizioneFruttoX > posizioneTestaX) {
     inputGiocatore2 = 's'; }
    else if (posizioneTestaX != 0 && grigliaGiocatore2[posizioneTestaX-1][posizioneTestaY].getBackground() != Color.BLUE) {
     inputGiocatore2 = 'w'; }
    else if (posizioneTestaX != 10 && grigliaGiocatore2[posizioneTestaX+1][posizioneTestaY].getBackground() != Color.BLUE) {
     inputGiocatore2 = 's'; }
    else {
     inputGiocatore2 = 'a'; } }
     
   else { // Calcola l'input se il frutto si trova alla stessa coordinata Y del serpente
    if (posizioneFruttoX > posizioneTestaX) { // Calcola l'input se il frutto si trova piu' in basso
     if (posizioneTestaX != 10 && grigliaGiocatore2[posizioneTestaX+1][posizioneTestaY].getBackground() != Color.BLUE && inputS >= inputD && inputS >= inputA) {
      inputGiocatore2 = 's'; }
     else if (posizioneTestaY != 20 && grigliaGiocatore2[posizioneTestaX][posizioneTestaY+1].getBackground() != Color.BLUE && inputD >= inputA) {
      inputGiocatore2 = 'd'; }
     else if (posizioneTestaY != 0 && grigliaGiocatore2[posizioneTestaX][posizioneTestaY-1].getBackground() != Color.BLUE && inputD <= inputA) {
      inputGiocatore2 = 'a'; }
     else if (posizioneTestaX != 0 && grigliaGiocatore2[posizioneTestaX-1][posizioneTestaY].getBackground() != Color.BLUE) {
      inputGiocatore2 = 'w'; }
     else if (posizioneTestaX != 10 && grigliaGiocatore2[posizioneTestaX+1][posizioneTestaY].getBackground() != Color.BLUE){
      inputGiocatore2 = 's';}
     else if (posizioneTestaY != 20 && grigliaGiocatore2[posizioneTestaX][posizioneTestaY+1].getBackground() != Color.BLUE && posizioneFruttoY > posizioneTestaY) {
      inputGiocatore2 = 'd'; }
     else if (posizioneTestaY != 0 && grigliaGiocatore2[posizioneTestaX][posizioneTestaY-1].getBackground() != Color.BLUE && posizioneFruttoY < posizioneTestaY) {
      inputGiocatore2 = 'a'; }
     else if (posizioneTestaY != 20 && grigliaGiocatore2[posizioneTestaX][posizioneTestaY+1].getBackground() != Color.BLUE) {
      inputGiocatore2 = 'd'; }
     else if (posizioneTestaY != 0 && grigliaGiocatore2[posizioneTestaX][posizioneTestaY-1].getBackground() != Color.BLUE) {
      inputGiocatore2 = 'a'; }
     else {
      inputGiocatore2 = 's'; } }
  
    else { // Calcola l'input se il frutto si trova piu' in alto
     if (posizioneTestaX != 0 && grigliaGiocatore2[posizioneTestaX-1][posizioneTestaY].getBackground() != Color.BLUE && inputW >= inputD && inputW >= inputA) {
      inputGiocatore2 = 'w'; }
     else if (posizioneTestaY != 20 && grigliaGiocatore2[posizioneTestaX][posizioneTestaY+1].getBackground() != Color.BLUE && inputD >= inputA) {
      inputGiocatore2 = 'd'; }
     else if (posizioneTestaY != 0 && grigliaGiocatore2[posizioneTestaX][posizioneTestaY-1].getBackground() != Color.BLUE && inputD <= inputA) {
      inputGiocatore2 = 'a'; }
     else if (posizioneTestaX != 10 && grigliaGiocatore2[posizioneTestaX+1][posizioneTestaY].getBackground() != Color.BLUE) {
      inputGiocatore2 = 's'; }
     else if (posizioneTestaX != 0 && grigliaGiocatore2[posizioneTestaX-1][posizioneTestaY].getBackground() != Color.BLUE) {
      inputGiocatore2 = 'w'; }
     else if (posizioneTestaY != 20 && grigliaGiocatore2[posizioneTestaX][posizioneTestaY+1].getBackground() != Color.BLUE && posizioneFruttoY > posizioneTestaY) {
      inputGiocatore2 = 'd'; }
     else if (posizioneTestaY != 0 && grigliaGiocatore2[posizioneTestaX][posizioneTestaY-1].getBackground() != Color.BLUE && posizioneFruttoY < posizioneTestaY) {
      inputGiocatore2 = 'a'; }
     else if (posizioneTestaY != 20 && grigliaGiocatore2[posizioneTestaX][posizioneTestaY+1].getBackground() != Color.BLUE) {
      inputGiocatore2 = 'd'; }
     else if (posizioneTestaY != 0 && grigliaGiocatore2[posizioneTestaX][posizioneTestaY-1].getBackground() != Color.BLUE) {
      inputGiocatore2 = 'a'; }
     else {
      inputGiocatore2 = 'w'; } } } }
  
  else if (swap == false && contatoreGiocatore2 > 3) { // Tipo AI 2
   if (posizioneFruttoX > posizioneTestaX) { // Calcola l'input se il frutto si trova piu' in basso
    if (posizioneTestaX != 10 && grigliaGiocatore2[posizioneTestaX+1][posizioneTestaY].getBackground() != Color.BLUE && inputS >= inputD && inputS >= inputA) {
     inputGiocatore2 = 's'; }
    else if (posizioneTestaY != 20 && grigliaGiocatore2[posizioneTestaX][posizioneTestaY+1].getBackground() != Color.BLUE && inputD >= inputA) {
     inputGiocatore2 = 'd'; }
    else if (posizioneTestaY != 0 && grigliaGiocatore2[posizioneTestaX][posizioneTestaY-1].getBackground() != Color.BLUE && inputD <= inputA) {
     inputGiocatore2 = 'a'; }
    else if (posizioneTestaX != 0 && grigliaGiocatore2[posizioneTestaX-1][posizioneTestaY].getBackground() != Color.BLUE) {
     inputGiocatore2 = 'w'; }
    else if (posizioneTestaX != 10 && grigliaGiocatore2[posizioneTestaX+1][posizioneTestaY].getBackground() != Color.BLUE){
     inputGiocatore2 = 's';}
    else if (posizioneTestaY != 20 && grigliaGiocatore2[posizioneTestaX][posizioneTestaY+1].getBackground() != Color.BLUE && posizioneFruttoY > posizioneTestaY) {
     inputGiocatore2 = 'd'; }
    else if (posizioneTestaY != 0 && grigliaGiocatore2[posizioneTestaX][posizioneTestaY-1].getBackground() != Color.BLUE && posizioneFruttoY < posizioneTestaY) {
     inputGiocatore2 = 'a'; }
    else if (posizioneTestaY != 20 && grigliaGiocatore2[posizioneTestaX][posizioneTestaY+1].getBackground() != Color.BLUE) {
     inputGiocatore2 = 'd'; }
    else if (posizioneTestaY != 0 && grigliaGiocatore2[posizioneTestaX][posizioneTestaY-1].getBackground() != Color.BLUE) {
     inputGiocatore2 = 'a'; }
    else {
     inputGiocatore2 = 's'; } }
   
   else if (posizioneFruttoX < posizioneTestaX) { // Calcola l'input se il frutto si trova piu' in alto
    if (posizioneTestaX != 0 && grigliaGiocatore2[posizioneTestaX-1][posizioneTestaY].getBackground() != Color.BLUE && inputW >= inputD && inputW >= inputA) {
     inputGiocatore2 = 'w'; }
    else if (posizioneTestaY != 20 && grigliaGiocatore2[posizioneTestaX][posizioneTestaY+1].getBackground() != Color.BLUE && inputD >= inputA) {
     inputGiocatore2 = 'd'; }
    else if (posizioneTestaY != 0 && grigliaGiocatore2[posizioneTestaX][posizioneTestaY-1].getBackground() != Color.BLUE && inputD <= inputA) {
     inputGiocatore2 = 'a'; }
    else if (posizioneTestaX != 10 && grigliaGiocatore2[posizioneTestaX+1][posizioneTestaY].getBackground() != Color.BLUE) {
     inputGiocatore2 = 's'; }
    else if (posizioneTestaX != 0 && grigliaGiocatore2[posizioneTestaX-1][posizioneTestaY].getBackground() != Color.BLUE) {
     inputGiocatore2 = 'w'; }
    else if (posizioneTestaY != 20 && grigliaGiocatore2[posizioneTestaX][posizioneTestaY+1].getBackground() != Color.BLUE && posizioneFruttoY > posizioneTestaY) {
     inputGiocatore2 = 'd'; }
    else if (posizioneTestaY != 0 && grigliaGiocatore2[posizioneTestaX][posizioneTestaY-1].getBackground() != Color.BLUE && posizioneFruttoY < posizioneTestaY) {
     inputGiocatore2 = 'a'; }
    else if (posizioneTestaY != 20 && grigliaGiocatore2[posizioneTestaX][posizioneTestaY+1].getBackground() != Color.BLUE) {
     inputGiocatore2 = 'd'; }
    else if (posizioneTestaY != 0 && grigliaGiocatore2[posizioneTestaX][posizioneTestaY-1].getBackground() != Color.BLUE) {
     inputGiocatore2 = 'a'; }
    else {
     inputGiocatore2 = 'w'; } }
   
   else { // Calcola l'input se il frutto ha la stessa altezza della testa del serpente
    if (posizioneFruttoY > posizioneTestaY) { // Calcola l'input se il frutto si trova piu' a destra del serpente
     if (posizioneTestaY != 20 && grigliaGiocatore2[posizioneTestaX][posizioneTestaY+1].getBackground() != Color.BLUE && inputD >= inputW && inputD >= inputS) { // guarda se andare a destra è la strada migliore
      inputGiocatore2 = 'd'; }
     else if (posizioneTestaX != 0 && grigliaGiocatore2[posizioneTestaX-1][posizioneTestaY].getBackground() != Color.BLUE && inputW >= inputS) {
      inputGiocatore2 = 'w'; }
     else if (posizioneTestaX != 10 && grigliaGiocatore2[posizioneTestaX+1][posizioneTestaY].getBackground() != Color.BLUE && inputW <= inputS) {
      inputGiocatore2 = 's'; }
     else if (posizioneTestaY != 0 && grigliaGiocatore2[posizioneTestaX][posizioneTestaY-1].getBackground() != Color.BLUE) {
      inputGiocatore2 = 'a'; }
     else if (posizioneTestaY != 20 && grigliaGiocatore2[posizioneTestaX][posizioneTestaY+1].getBackground() != Color.BLUE) {
      inputGiocatore2 = 'd'; }
     else if (posizioneTestaX != 0 && grigliaGiocatore2[posizioneTestaX-1][posizioneTestaY].getBackground() != Color.BLUE && posizioneFruttoX < posizioneTestaX) {
      inputGiocatore2 = 'w'; }
     else if (posizioneTestaX != 10 && grigliaGiocatore2[posizioneTestaX+1][posizioneTestaY].getBackground() != Color.BLUE && posizioneFruttoX > posizioneTestaX) {
      inputGiocatore2 = 's'; }
     else if (posizioneTestaX != 0 && grigliaGiocatore2[posizioneTestaX-1][posizioneTestaY].getBackground() != Color.BLUE) {
      inputGiocatore2 = 'w'; }
     else if (posizioneTestaX != 10 && grigliaGiocatore2[posizioneTestaX+1][posizioneTestaY].getBackground() != Color.BLUE) {
      inputGiocatore2 = 's'; }
     else {
      inputGiocatore2 = 'd'; } }
	 
    else { // Calcola l'input se il frutto si trova piu' a sinistra del serpente
     if (posizioneTestaY != 0 && grigliaGiocatore2[posizioneTestaX][posizioneTestaY-1].getBackground() != Color.BLUE && inputA >= inputW && inputA >= inputS) {
      inputGiocatore2 = 'a'; }
     else if (posizioneTestaX != 0 && grigliaGiocatore2[posizioneTestaX-1][posizioneTestaY].getBackground() != Color.BLUE && inputW >= inputS) {
      inputGiocatore2 = 'w'; }
     else if (posizioneTestaX != 10 && grigliaGiocatore2[posizioneTestaX+1][posizioneTestaY].getBackground() != Color.BLUE && inputW <= inputS) {
      inputGiocatore2 = 's'; }
     else if (posizioneTestaY != 20 && grigliaGiocatore2[posizioneTestaX][posizioneTestaY+1].getBackground() != Color.BLUE) {
      inputGiocatore2 = 'd'; }
     else if (posizioneTestaY != 0 && grigliaGiocatore2[posizioneTestaX][posizioneTestaY-1].getBackground() != Color.BLUE) {
      inputGiocatore2 = 'a'; }
     else if (posizioneTestaX != 0 && grigliaGiocatore2[posizioneTestaX-1][posizioneTestaY].getBackground() != Color.BLUE && posizioneFruttoX < posizioneTestaX) {
      inputGiocatore2 = 'w'; }
     else if (posizioneTestaX != 10 && grigliaGiocatore2[posizioneTestaX+1][posizioneTestaY].getBackground() != Color.BLUE && posizioneFruttoX > posizioneTestaX) {
      inputGiocatore2 = 's'; }
     else if (posizioneTestaX != 0 && grigliaGiocatore2[posizioneTestaX-1][posizioneTestaY].getBackground() != Color.BLUE) {
      inputGiocatore2 = 'w'; }
     else if (posizioneTestaX != 10 && grigliaGiocatore2[posizioneTestaX+1][posizioneTestaY].getBackground() != Color.BLUE) {
      inputGiocatore2 = 's'; }
     else {
      inputGiocatore2 = 'a'; } } } }
	  
  else if (contatoreGiocatore2 <= 3) { // Tipo IA 3
   if (posizioneFruttoY > posizioneTestaY) { // Calcola l'input se il frutto si trova piu' a destra del serpente
    if (posizioneTestaY != 20 && grigliaGiocatore2[posizioneTestaX][posizioneTestaY+1].getBackground() != Color.BLUE) {
     inputGiocatore2 = 'd'; }
    else {
     if (posizioneTestaX != 0 && grigliaGiocatore2[posizioneTestaX-1][posizioneTestaY].getBackground() != Color.BLUE) {
      inputGiocatore2 = 'w'; }
     else if (posizioneTestaX != 10 && grigliaGiocatore2[posizioneTestaX+1][posizioneTestaY].getBackground() != Color.BLUE) {
      inputGiocatore2 = 's'; }
     else {
      inputGiocatore2 = 'a'; } } }
  
   else if (posizioneFruttoY < posizioneTestaY) { // Calcola l'input se il frutto si trova piu' a sinistra del serpente
    if (posizioneTestaY != 0 && grigliaGiocatore2[posizioneTestaX][posizioneTestaY-1].getBackground() != Color.BLUE) {
     inputGiocatore2 = 'a'; }
    else {
     if (posizioneTestaX != 0 && grigliaGiocatore2[posizioneTestaX-1][posizioneTestaY].getBackground() != Color.BLUE) {
      inputGiocatore2 = 'w'; }
     else if (posizioneTestaX != 10 && grigliaGiocatore2[posizioneTestaX+1][posizioneTestaY].getBackground() != Color.BLUE) {
      inputGiocatore2 = 's'; }
     else {
      inputGiocatore2 = 'd'; } } }
	 
   else { // Calcola l'input se il frutto si trova alla stessa coordinata Y del serpente
    if (posizioneFruttoX > posizioneTestaX) { // Calcola l'input se il frutto si trova piu' in basso
     if (posizioneTestaX != 10 && grigliaGiocatore2[posizioneTestaX+1][posizioneTestaY].getBackground() != Color.BLUE) {
      inputGiocatore2 = 's'; }
     else {
      if (posizioneTestaY != 20 && grigliaGiocatore2[posizioneTestaX][posizioneTestaY+1].getBackground() != Color.BLUE) {
       inputGiocatore2 = 'd'; }
      else if (posizioneTestaY != 0 && grigliaGiocatore2[posizioneTestaX][posizioneTestaY-1].getBackground() != Color.BLUE) {
       inputGiocatore2 = 'a'; }
      else {
       inputGiocatore2 = 'w'; } } }
	  
    else { // Calcola l'input se il frutto si trova piu' in alto
     if (posizioneTestaX != 0 && grigliaGiocatore2[posizioneTestaX-1][posizioneTestaY].getBackground() != Color.BLUE) {
      inputGiocatore2 = 'w'; }
     else {
      if (posizioneTestaY != 20 && grigliaGiocatore2[posizioneTestaX][posizioneTestaY+1].getBackground() != Color.BLUE) {
       inputGiocatore2 = 'd'; }
      else if (posizioneTestaY != 0 && grigliaGiocatore2[posizioneTestaX][posizioneTestaY-1].getBackground() != Color.BLUE) {
       inputGiocatore2 = 'a'; }
      else {
       inputGiocatore2 = 's'; } } } } }
	
  contatoreSwap = contatoreSwap + 1;
  if (contatoreSwap == 1 && swap == true) {
   swap = !swap;
   contatoreSwap = 0; }
  else if (contatoreSwap == 1 && swap == false) {
   swap = !swap;
   contatoreSwap = 0; } }
 
 public int calcolaBlocchiBianchi(char c) { // Conta il numero di spazi bianchi dalla testa del serpente fino alla fine della griglia per scegliere una direzione migliore
  boolean flag = true;
  boolean flag2 = true;
  boolean flag3 = true;
  int spaziBianchi = 0;
  int puntiFrutto = 100;
  try {
   //////
   if (c == 'w') { // Controlla lo spazio se il serpente si deve muove in alto
    for (int i = posizioneTestaX - 1; i >= 0; i--) { // Scorre la griglia finche' non finisce sul bordo
	 if (flag == true) {
	  if (grigliaGiocatore2[i][posizioneTestaY].getBackground() != Color.BLUE && i != 0) { // Controlla se la casella e' vuota
	   if (grigliaGiocatore2[i][posizioneTestaY].getBackground() == Color.WHITE) {
	    spaziBianchi = spaziBianchi + 1; }
	   else {
	    spaziBianchi = spaziBianchi + puntiFrutto; } }
	   
	  else if (grigliaGiocatore2[i][posizioneTestaY].getBackground() != Color.BLUE && i == 0) { // Controlla se e' arrivato alla fine della griglia, dopodiche' comincia a sondare le zone adiacenti
	   flag = false;
	   if (grigliaGiocatore2[i][posizioneTestaY].getBackground() == Color.WHITE) {
	    spaziBianchi = spaziBianchi + 1; }
	   else {
	    spaziBianchi = spaziBianchi + puntiFrutto; }
	   for (int j = posizioneTestaY-1; j >= 0; j--) { // Sonda la zona a sinistra
	    if (flag2 == true) {
		 if (grigliaGiocatore2[i][j].getBackground() != Color.BLUE) { 
		  if (grigliaGiocatore2[i][j].getBackground() == Color.WHITE) {
		   spaziBianchi = spaziBianchi + 1; }
		  else {
		   spaziBianchi = spaziBianchi + puntiFrutto; } }
		 else { 
		  flag2 = false; } } }
	   for (int j = posizioneTestaY+1; j < 21; j++) { // Controlla la zona a destra
	    if (flag3 == true) {
		 if (grigliaGiocatore2[i][j].getBackground() != Color.BLUE) {
		  if (grigliaGiocatore2[i][j].getBackground() == Color.WHITE) {
		   spaziBianchi = spaziBianchi + 1; }
		  else {
		   spaziBianchi = spaziBianchi + puntiFrutto; } }
		 else {
		  flag3 = false; } } } }
	   
	  else if (grigliaGiocatore2[i][posizioneTestaY].getBackground() == Color.BLUE) { // Se trova un blocco blu si ferma per controllare le zone adiacenti
	   flag = false;
	   if (spaziBianchi != 0) {
	    for (int j = posizioneTestaY-1; j >= 0; j--) {
	     if (flag2 == true) {
		  if (grigliaGiocatore2[i+1][j].getBackground() != Color.BLUE) { 
		   if (grigliaGiocatore2[i+1][j].getBackground() == Color.WHITE) {
		    spaziBianchi = spaziBianchi + 1; }
		   else {
		    spaziBianchi = spaziBianchi + puntiFrutto; } }
	     else {
		  flag2 = false; } } }
	    for (int j = posizioneTestaY+1; j < 21; j++) {
	     if (flag3 == true) {
		  if (grigliaGiocatore2[i+1][j].getBackground() != Color.BLUE) {
		   if (grigliaGiocatore2[i+1][j].getBackground() == Color.WHITE) {
		    spaziBianchi = spaziBianchi + 1; }
		   else {
		    spaziBianchi = spaziBianchi + puntiFrutto; } }
		  else {
		   flag3 = false; } } } } } } } }

   //////	
   else if (c == 's') {
    for (int i = posizioneTestaX+1; i < 11; i++) {
	 if (flag == true) {
	  if (grigliaGiocatore2[i][posizioneTestaY].getBackground() != Color.BLUE && i != 10) { 
	   if (grigliaGiocatore2[i][posizioneTestaY].getBackground() == Color.WHITE) {
		spaziBianchi = spaziBianchi + 1; }
	   else {
		spaziBianchi = spaziBianchi + puntiFrutto; } }
	   
	  else if (grigliaGiocatore2[i][posizioneTestaY].getBackground() != Color.BLUE && i == 10) {
	   flag = false;
	   if (grigliaGiocatore2[i][posizioneTestaY].getBackground() == Color.WHITE) {
	    spaziBianchi = spaziBianchi + 1; }
	   else {
	    spaziBianchi = spaziBianchi + puntiFrutto; }
	   for (int j = posizioneTestaY-1; j >= 0; j--) { 
	    if (flag2 == true) {
		 if (grigliaGiocatore2[i][j].getBackground() != Color.BLUE) { 
		  if (grigliaGiocatore2[i][j].getBackground() == Color.WHITE) {
		   spaziBianchi = spaziBianchi + 1; }
		  else {
		   spaziBianchi = spaziBianchi + puntiFrutto; } }
		 else { 
		  flag2 = false; } } }
	   for (int j = posizioneTestaY+1; j < 21; j++) { 
	    if (flag3 == true) {
		 if (grigliaGiocatore2[i][j].getBackground() != Color.BLUE) {
		  if (grigliaGiocatore2[i][j].getBackground() == Color.WHITE) {
		    spaziBianchi = spaziBianchi + 1; }
		   else {
		    spaziBianchi = spaziBianchi + puntiFrutto; } }
		 else {
		  flag3 = false; } } } }
	   
	  else if (grigliaGiocatore2[i][posizioneTestaY].getBackground() == Color.BLUE) { 
	   flag = false;
	   if (spaziBianchi != 0) {
	    for (int j = posizioneTestaY-1; j >= 0; j--) {
	     if (flag2 == true) {
		  if (grigliaGiocatore2[i-1][j].getBackground() != Color.BLUE) { 
		   if (grigliaGiocatore2[i-1][j].getBackground() == Color.WHITE) {
		    spaziBianchi = spaziBianchi + 1; }
		   else {
		    spaziBianchi = spaziBianchi + puntiFrutto; } }
		  else {
		   flag2 = false; } } }
	    for (int j = posizioneTestaY+1; j < 21; j++) {
	     if (flag3 == true) {
		  if (grigliaGiocatore2[i-1][j].getBackground() != Color.BLUE) {
		   if (grigliaGiocatore2[i-1][j].getBackground() == Color.WHITE) {
		    spaziBianchi = spaziBianchi + 1; }
		   else {
		    spaziBianchi = spaziBianchi + puntiFrutto; } }
		  else {
		   flag3 = false; } } } } } } } }
   
   //////   
   else if (c == 'a') {
	for (int i = posizioneTestaY-1; i >= 0; i--) {
	 if (flag == true) {
	  if (grigliaGiocatore2[posizioneTestaX][i].getBackground() != Color.BLUE && i != 0) {
	   if (grigliaGiocatore2[posizioneTestaX][i].getBackground() == Color.WHITE) {
		spaziBianchi = spaziBianchi + 1; }
	   else {
		spaziBianchi = spaziBianchi + puntiFrutto; } }
	  
	  else if (grigliaGiocatore2[posizioneTestaX][i].getBackground() != Color.BLUE && i == 0) {
	   flag = false;
	   if (grigliaGiocatore2[posizioneTestaX][i].getBackground() == Color.WHITE) {
	    spaziBianchi = spaziBianchi + 1; }
	   else {
	    spaziBianchi = spaziBianchi + puntiFrutto; }
	   for (int j = posizioneTestaX-1; j >= 0; j--) {
	    if (flag2 == true) {
		 if (grigliaGiocatore2[j][i].getBackground() != Color.BLUE) { 
		  if (grigliaGiocatore2[j][i].getBackground() == Color.WHITE) {
		    spaziBianchi = spaziBianchi + 1; }
		   else {
		    spaziBianchi = spaziBianchi + puntiFrutto; } }
		 else { 
		  flag2 = false; } } }
	   for (int j = posizioneTestaX+1; j < 11; j++) { 
	    if (flag3 == true) {
		 if (grigliaGiocatore2[j][i].getBackground() != Color.BLUE) {
		  if (grigliaGiocatore2[j][i].getBackground() == Color.WHITE) {
		   spaziBianchi = spaziBianchi + 1; }
		  else {
		   spaziBianchi = spaziBianchi + puntiFrutto; } }
		 else {
		  flag3 = false; } } } }
	    
	  else if (grigliaGiocatore2[posizioneTestaX][i].getBackground() == Color.BLUE) {
	   flag = false;
	   if (spaziBianchi != 0) {
	   for (int j = posizioneTestaX-1; j >= 0; j--) {
	    if (flag2 == true) {
		 if (grigliaGiocatore2[j][i+1].getBackground() != Color.BLUE) { 
		  if (grigliaGiocatore2[j][i+1].getBackground() == Color.WHITE) {
		   spaziBianchi = spaziBianchi + 1; }
		  else {
		   spaziBianchi = spaziBianchi + puntiFrutto; } }
		  else { 
		   flag2 = false; } } }
	    for (int j = posizioneTestaX+1; j < 11; j++) {
	     if (flag3 == true) {
		  if (grigliaGiocatore2[j][i+1].getBackground() != Color.BLUE) {
		   if (grigliaGiocatore2[j][i+1].getBackground() == Color.WHITE) {
		    spaziBianchi = spaziBianchi + 1; }
		   else {
		    spaziBianchi = spaziBianchi + puntiFrutto; } }
		  else {
		   flag3 = false; } } } } } } } }
		   
  //////  
  else if (c == 'd') {
	for (int i = posizioneTestaY+1; i < 21; i++) {
	 if (flag == true) {
	  if (grigliaGiocatore2[posizioneTestaX][i].getBackground() != Color.BLUE && i != 20) {
	   if (grigliaGiocatore2[posizioneTestaX][i].getBackground() == Color.WHITE) {
		spaziBianchi = spaziBianchi + 1; }
	   else {
		spaziBianchi = spaziBianchi + puntiFrutto; } }
	  
	  else if (grigliaGiocatore2[posizioneTestaX][i].getBackground() != Color.BLUE && i == 20) {
	   flag = false;
	   if (grigliaGiocatore2[posizioneTestaX][i].getBackground() == Color.WHITE) {
	    spaziBianchi = spaziBianchi + 1; }
	   else {
	    spaziBianchi = spaziBianchi + puntiFrutto; }
	   for (int j = posizioneTestaX-1; j >= 0; j--) {
	    if (flag2 == true) {
		 if (grigliaGiocatore2[j][i].getBackground() != Color.BLUE) { 
		  if (grigliaGiocatore2[j][i].getBackground() == Color.WHITE) {
		   spaziBianchi = spaziBianchi + 1; }
		  else {
		   spaziBianchi = spaziBianchi + puntiFrutto; } }
		 else { 
		  flag2 = false; } } }
	   for (int j = posizioneTestaX+1; j < 11; j++) {
	    if (flag3 == true) {
		 if (grigliaGiocatore2[j][i].getBackground() != Color.BLUE) {
		  if (grigliaGiocatore2[j][i].getBackground() == Color.WHITE) {
		   spaziBianchi = spaziBianchi + 1; }
		  else {
		   spaziBianchi = spaziBianchi + puntiFrutto; } }
		 else {
		  flag3 = false; } } } }
	    
	  else if (grigliaGiocatore2[posizioneTestaX][i].getBackground() == Color.BLUE) {
	   flag = false;
	   if (spaziBianchi != 0) {
	   for (int j = posizioneTestaX-1; j >= 0; j--) {
	    if (flag2 == true) {
		 if (grigliaGiocatore2[j][i-1].getBackground() != Color.BLUE) { 
		  if (grigliaGiocatore2[j][i-1].getBackground() == Color.WHITE) {
		   spaziBianchi = spaziBianchi + 1; }
		  else {
		   spaziBianchi = spaziBianchi + puntiFrutto; } }
		  else { 
		   flag2 = false; } } }
	    for (int j = posizioneTestaX+1; j < 11; j++) {
	     if (flag3 == true) {
		  if (grigliaGiocatore2[j][i-1].getBackground() != Color.BLUE) {
		   if (grigliaGiocatore2[j][i-1].getBackground() == Color.WHITE) {
		    spaziBianchi = spaziBianchi + 1; }
		   else {
		    spaziBianchi = spaziBianchi + puntiFrutto; } }
		  else {
		   flag3 = false; } } } } } } } }
  return spaziBianchi;	}
   
  catch(ArrayIndexOutOfBoundsException e) { 
   return spaziBianchi; } }
 
 public void aggiornaPunteggioGiocatore1() {
  contatoreGiocatore1 = contatoreGiocatore1 + 1;
  nomeGiocatore1.setText("Griglia del Giocatore 1                                      Punteggio: " + contatoreGiocatore1); }
 
 public void aggiornaPunteggioGiocatore2() {
  contatoreGiocatore2 = contatoreGiocatore2 + 1;
  if (modalita == false) {
   nomeGiocatore2.setText("Griglia del Computer                                      Punteggio: " + contatoreGiocatore2); }
  else {
   nomeGiocatore2.setText("Griglia del Giocatore 2                                      Punteggio: " + contatoreGiocatore2); } }

 public void gameOver() {
  String[] opzioni = {"Nuova Partita", "Menu"};
  JLabel risultato;
  if (contatoreGiocatore1 > contatoreGiocatore2) {
   risultato = new JLabel("Game Over! Vince il Giocatore 1 con " + contatoreGiocatore1 + " punti!", JLabel.CENTER); }
  else if (contatoreGiocatore2 > contatoreGiocatore1) {
   if (modalita == true) {
    risultato = new JLabel("Game Over! Vince il Giocatore 2 con " + contatoreGiocatore2 + " punti!", JLabel.CENTER); }
   else {
    risultato = new JLabel("Game Over! Vince il Computer con " + contatoreGiocatore2 + " punti!", JLabel.CENTER); } }
  else {
   risultato = new JLabel("Game Over! Pareggio con " + " punti!"); }
  int scelta;
  if (modalita == true) {
   scelta = JOptionPane.showOptionDialog(null, risultato, "SNAKE - Player VS Player", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, opzioni, opzioni[0]); }
  else {
   scelta = JOptionPane.showOptionDialog(null, risultato, "SNAKE - Player VS Computer", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, opzioni, opzioni[0]); }
  if (scelta == 0) {
   inizioNuovaPartita(); }
  else {
   apriMenu();  } }
 
 public void settaTimer() { 
  if (giocatore1GameOver == false) {
   timerGiocatore1 = new java.util.Timer();
   timerGiocatore1.schedule(new giocoGiocatore1(), 0, 100); }
  if (giocatore2GameOver == false) {
   timerGiocatore2 = new java.util.Timer();
   timerGiocatore2.schedule(new giocoGiocatore2(), 0, 100);  } }
 
 public void apriMenu() {
  Menu menu = new Menu(overheadMenu1, overheadMenu2);
  menu.setSize(510,290);
  menu.setVisible(true);
  menu.setResizable(false);
  menu.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
  setVisible(false);
  dispose();  }
 
 public void inizioNuovaPartita() { 
  Vs nuovaPartita = new Vs(overheadMenu1, overheadMenu2, modalita);
  nuovaPartita.setSize(435, 535);
  nuovaPartita.setResizable(false);
  nuovaPartita.setVisible(true);
  nuovaPartita.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
  setVisible(false);
  dispose(); } }
