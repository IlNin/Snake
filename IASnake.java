import javax.swing.*;
import java.util.*;
import java.awt.*;
import java.awt.*;
import java.awt.event.*;
import java.beans.*;

public class IASnake extends JFrame {
 JPanel pannello; // Pannello principale a cui verranno aggiunti tutti gli altri componenti
 JPanel p; // Panello dove verra' mostrata la griglia del gioco
 JLabel punteggio; // Contiene il punteggio attuale dell'IA
 JLabel record; // Contiene il punteggio record
 int contatore = 0; // Numero di frutti mangiati
 int contatoreRecord; // Record di frutti mangiati
 JLabel[][] griglia; // Griglia di gioco
 int posizioneFruttoX; // Coordinata X del frutto attuale
 int posizioneFruttoY; // Coordinata Y del frutto attuale
 int posizioneTestaX = 10; // Coordinata X della testa del serpente
 int posizioneTestaY = 6; // Coordinata Y della testa del serpente
 Snake snake; // Serpente iniziale
 char input; // Indica la direzione dello spostamento del serpente
 java.util.Timer timer; // Timer che permette lo spostamento del serpente
 java.util.Timer timerLoop; // Timer che entra in azione se la CPU entra in un loop
 boolean pausa = false; // Indica se il gioco e' in pausa
 boolean benchmark; // Indica se il programma deve essere avviato in modalità benchmark
 int numeroPartite; // Indica quante partite sono state effettuate nella sessione. Utile per il benchmark
 int sommaTotale; // La somma di tutti i punteggi nella sessione. Questo dato, insieme al numero di partite, dara' la media. Utile per il benchmark
 int overheadMenu; // Informazioni importanti per il menuù
 boolean swap = true; // Indica il tipo di IA che la CPU deve utilizzare
 int contatoreSwap = 0; // Quando raggiunge una certa cifra parte lo swap
 boolean flagLoop = false; // Si attiva in caso di loop
 
 public IASnake(int contatoreRecord, int overheadMenu, int numeroPartite, int sommaTotale, boolean benchmark) {
  this.numeroPartite = numeroPartite;
  this.sommaTotale = sommaTotale;
  this.contatoreRecord = contatoreRecord;
  this.overheadMenu = overheadMenu;
  this.benchmark = benchmark;
  if (benchmark == false) {
   this.setTitle("SNAKE - Test IA Normale - In Gioco"); }
  else {
   this.setTitle("SNAKE - Test IA Benchmark - Partita " + numeroPartite); }
  setFocusable(true); // Essere "focusable" permette di ricevere input
  requestFocus();
  
  pannello = new JPanel(); // Crea il pannello principale a cui tutti i componenti si attaccheranno. In particolare un altro pannello e due JLabels
  pannello.setLayout(new BoxLayout(pannello, BoxLayout.PAGE_AXIS));
  this.setContentPane(pannello);
  
  p = new JPanel(); // Crea il pannello dove viene visualizzato il gioco
  p.setBackground(Color.BLACK);
  p.setVisible(true);
  pannello.add(p);
  
  punteggio = new JLabel("Punteggio dell'IA: " + contatore); // Crea la JLabel dove viene visualizzato il punteggio attuale
  punteggio.setAlignmentX(Component.CENTER_ALIGNMENT);
  pannello.add(punteggio);
  
  if (benchmark == false) { // Crea la JLabel dove viene visualizzato il record attuale
   record = new JLabel("Record dell'IA: " + contatoreRecord); }
  else {
   if (numeroPartite != 1) {
    record = new JLabel("Record dell'IA: " + contatoreRecord + "        Media dell'IA: " + sommaTotale/(numeroPartite-1)); } 
   else {
    record = new JLabel("Record dell'IA: " + contatoreRecord + "        Media dell'IA: 0"); } } 
  record.setAlignmentX(Component.CENTER_ALIGNMENT);
  pannello.add(record);
  
  creaGriglia(); // Prepara il gioco
  snake = new Snake();
  inserisciFrutto();
  timer = new java.util.Timer();
  timerLoop = new java.util.Timer();
  if (benchmark == false) {
   timer.schedule(new gioco(), 0, 50);
   timerLoop.schedule(new loop(), 10000, 10000); }
  else { // In modalita' "banchmark" la velocita' di gioco e' rapidissima
   timer.scheduleAtFixedRate(new gioco(), 0, 1);
   timerLoop.scheduleAtFixedRate(new loop(), 1000, 1000); }
  
  this.addKeyListener(new KeyAdapter() { // Permette di accettare i comandi della tastiera come input
   public void keyTyped(KeyEvent e) {;
    if (e.getKeyChar() == 'e' && benchmark == true) { // "e" permette di uscire dalla modalita' "benchmark"
	 timer.cancel();
	 timerLoop.cancel();
	 apriMenu(); }
    else if (e.getKeyChar() == 'p' && pausa == false && benchmark == false) { // Mette il gioco in pausa
	 timer.cancel();
	 timerLoop.cancel();
	 setTitle("SNAKE - Test IA Normale - In Pausa");
	 pausa = true; }
	else if (e.getKeyChar() == 'e' && pausa == true && benchmark == false) { // Torna al menu dalla pausa
	 apriMenu(); }
	else if (e.getKeyChar() == 'p' && pausa == true && benchmark == false) { // Fa uscire il gioco dalla pausa
	 setTitle("SNAKE - Test IA Normale - In Gioco");
	 pausa = false;
	 timer = new java.util.Timer();
	 timerLoop = new java.util.Timer();
	 timer.schedule(new gioco(), 0, 50);
     timerLoop.schedule(new loop(), 10000, 10000);	 } } }); }
  
 class gioco extends TimerTask { // Al termine del timer, il serpente viene mosso
  public void run() {
   scegliInput();
   boolean esito = muoviSnake();
   if (esito == false) { // Il serpente si scontra contro qualcosa e il gioco termina
    timer.cancel();
    timerLoop.cancel();
    gameOver(); } } } // il timer viene terminato
 
 class loop extends TimerTask { // Viene avviato se il computer entra in loop
  public void run() { 
   flagLoop = true; } }

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
	posizioneFruttoX = X;
	posizioneFruttoY = Y;
    flag = true; } } }
	
 public void scegliInput() { // Permetta all'IA di scegliere una nuova direzione
  int inputW = calcolaBlocchiBianchi('w'); // Controlla lo spazio che c'e' in alto
  int inputD = calcolaBlocchiBianchi('d'); // Controlla lo spazio che c'e' a destra
  int inputS = calcolaBlocchiBianchi('s'); // Controlla lo spazio che c'e' sotto
  int inputA = calcolaBlocchiBianchi('a'); // Controlla lo spazio che c'e' a sinistra
  if (swap == true && flagLoop == false && contatore > 3) { // tipo AI 1
   if (posizioneFruttoY > posizioneTestaY) { // Calcola l'input se il frutto si trova piu' a destra del serpente
    if (posizioneTestaY != 20 && griglia[posizioneTestaX][posizioneTestaY+1].getBackground() != Color.BLUE && inputD >= inputW && inputD >= inputS) { // Guarda se andare a destra è la strada migliore
     input = 'd'; }
    else if (posizioneTestaX != 0 && griglia[posizioneTestaX-1][posizioneTestaY].getBackground() != Color.BLUE && inputW >= inputS) {
     input = 'w'; }
    else if (posizioneTestaX != 10 && griglia[posizioneTestaX+1][posizioneTestaY].getBackground() != Color.BLUE && inputW <= inputS) {
     input = 's'; }
    else if (posizioneTestaY != 0 && griglia[posizioneTestaX][posizioneTestaY-1].getBackground() != Color.BLUE) {
     input = 'a'; }
    else if (posizioneTestaY != 20 && griglia[posizioneTestaX][posizioneTestaY+1].getBackground() != Color.BLUE) {
	 input = 'd'; }
	else if (posizioneTestaX != 0 && griglia[posizioneTestaX-1][posizioneTestaY].getBackground() != Color.BLUE && posizioneFruttoX < posizioneTestaX) {
	 input = 'w'; }
	else if (posizioneTestaX != 10 && griglia[posizioneTestaX+1][posizioneTestaY].getBackground() != Color.BLUE && posizioneFruttoX > posizioneTestaX) {
	 input = 's'; }
    else if (posizioneTestaX != 0 && griglia[posizioneTestaX-1][posizioneTestaY].getBackground() != Color.BLUE) {
	 input = 'w'; }
	else if (posizioneTestaX != 10 && griglia[posizioneTestaX+1][posizioneTestaY].getBackground() != Color.BLUE) {
	 input = 's'; }
    else {
     input = 'd'; } }
	 
   else if (posizioneFruttoY < posizioneTestaY) { // Calcola l'input se il frutto si trova piu' a sinistra del serpente
    if (posizioneTestaY != 0 && griglia[posizioneTestaX][posizioneTestaY-1].getBackground() != Color.BLUE && inputA >= inputW && inputA >= inputS) {
	 input = 'a'; }
	else if (posizioneTestaX != 0 && griglia[posizioneTestaX-1][posizioneTestaY].getBackground() != Color.BLUE && inputW >= inputS) {
	 input = 'w'; }
	else if (posizioneTestaX != 10 && griglia[posizioneTestaX+1][posizioneTestaY].getBackground() != Color.BLUE && inputW <= inputS) {
	 input = 's'; }
	else if (posizioneTestaY != 20 && griglia[posizioneTestaX][posizioneTestaY+1].getBackground() != Color.BLUE) {
	 input = 'd'; }
	else if (posizioneTestaY != 0 && griglia[posizioneTestaX][posizioneTestaY-1].getBackground() != Color.BLUE) {
	 input = 'a'; }
	else if (posizioneTestaX != 0 && griglia[posizioneTestaX-1][posizioneTestaY].getBackground() != Color.BLUE && posizioneFruttoX < posizioneTestaX) {
	 input = 'w'; }
	else if (posizioneTestaX != 10 && griglia[posizioneTestaX+1][posizioneTestaY].getBackground() != Color.BLUE && posizioneFruttoX > posizioneTestaX) {
	 input = 's'; }
    else if (posizioneTestaX != 0 && griglia[posizioneTestaX-1][posizioneTestaY].getBackground() != Color.BLUE) {
	 input = 'w'; }
	else if (posizioneTestaX != 10 && griglia[posizioneTestaX+1][posizioneTestaY].getBackground() != Color.BLUE) {
	 input = 's'; }
	else {
	 input = 'a'; } }
     
   else { // Calcola l'input se il frutto si trova alla stessa coordinata Y del serpente
    if (posizioneFruttoX > posizioneTestaX) { // Calcola l'input se il frutto si trova piu' in basso
	 if (posizioneTestaX != 10 && griglia[posizioneTestaX+1][posizioneTestaY].getBackground() != Color.BLUE && inputS >= inputD && inputS >= inputA) {
	  input = 's'; }
	 else if (posizioneTestaY != 20 && griglia[posizioneTestaX][posizioneTestaY+1].getBackground() != Color.BLUE && inputD >= inputA) {
	  input = 'd'; }
	 else if (posizioneTestaY != 0 && griglia[posizioneTestaX][posizioneTestaY-1].getBackground() != Color.BLUE && inputD <= inputA) {
	  input = 'a'; }
	 else if (posizioneTestaX != 0 && griglia[posizioneTestaX-1][posizioneTestaY].getBackground() != Color.BLUE) {
	  input = 'w'; }
	 else if (posizioneTestaX != 10 && griglia[posizioneTestaX+1][posizioneTestaY].getBackground() != Color.BLUE){
	  input = 's';}
	 else if (posizioneTestaY != 20 && griglia[posizioneTestaX][posizioneTestaY+1].getBackground() != Color.BLUE && posizioneFruttoY > posizioneTestaY) {
	  input = 'd'; }
	 else if (posizioneTestaY != 0 && griglia[posizioneTestaX][posizioneTestaY-1].getBackground() != Color.BLUE && posizioneFruttoY < posizioneTestaY) {
	  input = 'a'; }
	 else if (posizioneTestaY != 20 && griglia[posizioneTestaX][posizioneTestaY+1].getBackground() != Color.BLUE) {
	  input = 'd'; }
	 else if (posizioneTestaY != 0 && griglia[posizioneTestaX][posizioneTestaY-1].getBackground() != Color.BLUE) {
	  input = 'a'; }
	 else {
	  input = 's'; } }
  
    else { // Calcola l'input se il frutto si trova piu' in alto
	 if (posizioneTestaX != 0 && griglia[posizioneTestaX-1][posizioneTestaY].getBackground() != Color.BLUE && inputW >= inputD && inputW >= inputA) {
	  input = 'w'; }
	 else if (posizioneTestaY != 20 && griglia[posizioneTestaX][posizioneTestaY+1].getBackground() != Color.BLUE && inputD >= inputA) {
	  input = 'd'; }
	 else if (posizioneTestaY != 0 && griglia[posizioneTestaX][posizioneTestaY-1].getBackground() != Color.BLUE && inputD <= inputA) {
	  input = 'a'; }
     else if (posizioneTestaX != 10 && griglia[posizioneTestaX+1][posizioneTestaY].getBackground() != Color.BLUE) {
      input = 's'; }
	 else if (posizioneTestaX != 0 && griglia[posizioneTestaX-1][posizioneTestaY].getBackground() != Color.BLUE) {
	  input = 'w'; }
	 else if (posizioneTestaY != 20 && griglia[posizioneTestaX][posizioneTestaY+1].getBackground() != Color.BLUE && posizioneFruttoY > posizioneTestaY) {
	  input = 'd'; }
	 else if (posizioneTestaY != 0 && griglia[posizioneTestaX][posizioneTestaY-1].getBackground() != Color.BLUE && posizioneFruttoY < posizioneTestaY) {
	  input = 'a'; }
	 else if (posizioneTestaY != 20 && griglia[posizioneTestaX][posizioneTestaY+1].getBackground() != Color.BLUE) {
	  input = 'd'; }
	 else if (posizioneTestaY != 0 && griglia[posizioneTestaX][posizioneTestaY-1].getBackground() != Color.BLUE) {
	  input = 'a'; }
     else {
      input = 'w'; } } } }
  
  else if (swap == false && flagLoop == false && contatore > 3) { // tipo AI 2
   if (posizioneFruttoX > posizioneTestaX) { // Calcola l'input se il frutto si trova piu' in basso
	if (posizioneTestaX != 10 && griglia[posizioneTestaX+1][posizioneTestaY].getBackground() != Color.BLUE && inputS >= inputD && inputS >= inputA) {
	 input = 's'; }
	else if (posizioneTestaY != 20 && griglia[posizioneTestaX][posizioneTestaY+1].getBackground() != Color.BLUE && inputD >= inputA) {
	 input = 'd'; }
	else if (posizioneTestaY != 0 && griglia[posizioneTestaX][posizioneTestaY-1].getBackground() != Color.BLUE && inputD <= inputA) {
	 input = 'a'; }
	else if (posizioneTestaX != 0 && griglia[posizioneTestaX-1][posizioneTestaY].getBackground() != Color.BLUE) {
	 input = 'w'; }
    else if (posizioneTestaX != 10 && griglia[posizioneTestaX+1][posizioneTestaY].getBackground() != Color.BLUE){
	 input = 's';}
    else if (posizioneTestaY != 20 && griglia[posizioneTestaX][posizioneTestaY+1].getBackground() != Color.BLUE && posizioneFruttoY > posizioneTestaY) {
	 input = 'd'; }
	else if (posizioneTestaY != 0 && griglia[posizioneTestaX][posizioneTestaY-1].getBackground() != Color.BLUE && posizioneFruttoY < posizioneTestaY) {
	 input = 'a'; }
	else if (posizioneTestaY != 20 && griglia[posizioneTestaX][posizioneTestaY+1].getBackground() != Color.BLUE) {
	 input = 'd'; }
	else if (posizioneTestaY != 0 && griglia[posizioneTestaX][posizioneTestaY-1].getBackground() != Color.BLUE) {
	 input = 'a'; }
	else {
	 input = 's'; } }
   
   else if (posizioneFruttoX < posizioneTestaX) { // Calcola l'input se il frutto si trova piu' in alto
	if (posizioneTestaX != 0 && griglia[posizioneTestaX-1][posizioneTestaY].getBackground() != Color.BLUE && inputW >= inputD && inputW >= inputA) {
	 input = 'w'; }
	else if (posizioneTestaY != 20 && griglia[posizioneTestaX][posizioneTestaY+1].getBackground() != Color.BLUE && inputD >= inputA) {
	 input = 'd'; }
	else if (posizioneTestaY != 0 && griglia[posizioneTestaX][posizioneTestaY-1].getBackground() != Color.BLUE && inputD <= inputA) {
	 input = 'a'; }
    else if (posizioneTestaX != 10 && griglia[posizioneTestaX+1][posizioneTestaY].getBackground() != Color.BLUE) {
     input = 's'; }
	else if (posizioneTestaX != 0 && griglia[posizioneTestaX-1][posizioneTestaY].getBackground() != Color.BLUE) {
	 input = 'w'; }
    else if (posizioneTestaY != 20 && griglia[posizioneTestaX][posizioneTestaY+1].getBackground() != Color.BLUE && posizioneFruttoY > posizioneTestaY) {
	 input = 'd'; }
	else if (posizioneTestaY != 0 && griglia[posizioneTestaX][posizioneTestaY-1].getBackground() != Color.BLUE && posizioneFruttoY < posizioneTestaY) {
	 input = 'a'; }
	else if (posizioneTestaY != 20 && griglia[posizioneTestaX][posizioneTestaY+1].getBackground() != Color.BLUE) {
	 input = 'd'; }
	else if (posizioneTestaY != 0 && griglia[posizioneTestaX][posizioneTestaY-1].getBackground() != Color.BLUE) {
	 input = 'a'; }
    else {
     input = 'w'; } }
   
   else { // Calcola l'input se il frutto ha la stessa altezza della testa del serpente
    if (posizioneFruttoY > posizioneTestaY) { // Calcola l'input se il frutto si trova piu' a destra del serpente
     if (posizioneTestaY != 20 && griglia[posizioneTestaX][posizioneTestaY+1].getBackground() != Color.BLUE && inputD >= inputW && inputD >= inputS) { // guarda se andare a destra è la strada migliore
      input = 'd'; }
     else if (posizioneTestaX != 0 && griglia[posizioneTestaX-1][posizioneTestaY].getBackground() != Color.BLUE && inputW >= inputS) {
      input = 'w'; }
     else if (posizioneTestaX != 10 && griglia[posizioneTestaX+1][posizioneTestaY].getBackground() != Color.BLUE && inputW <= inputS) {
      input = 's'; }
     else if (posizioneTestaY != 0 && griglia[posizioneTestaX][posizioneTestaY-1].getBackground() != Color.BLUE) {
      input = 'a'; }
	 else if (posizioneTestaY != 20 && griglia[posizioneTestaX][posizioneTestaY+1].getBackground() != Color.BLUE) {
	  input = 'd'; }
     else if (posizioneTestaX != 0 && griglia[posizioneTestaX-1][posizioneTestaY].getBackground() != Color.BLUE && posizioneFruttoX < posizioneTestaX) {
	  input = 'w'; }
	 else if (posizioneTestaX != 10 && griglia[posizioneTestaX+1][posizioneTestaY].getBackground() != Color.BLUE && posizioneFruttoX > posizioneTestaX) {
	  input = 's'; }
	 else if (posizioneTestaX != 0 && griglia[posizioneTestaX-1][posizioneTestaY].getBackground() != Color.BLUE) {
	  input = 'w'; }
	 else if (posizioneTestaX != 10 && griglia[posizioneTestaX+1][posizioneTestaY].getBackground() != Color.BLUE) {
	  input = 's'; }
     else {
      input = 'd'; } }
	 
    else { // Calcola l'input se il frutto si trova piu' a sinistra del serpente
     if (posizioneTestaY != 0 && griglia[posizioneTestaX][posizioneTestaY-1].getBackground() != Color.BLUE && inputA >= inputW && inputA >= inputS) {
	  input = 'a'; }
	 else if (posizioneTestaX != 0 && griglia[posizioneTestaX-1][posizioneTestaY].getBackground() != Color.BLUE && inputW >= inputS) {
	  input = 'w'; }
	 else if (posizioneTestaX != 10 && griglia[posizioneTestaX+1][posizioneTestaY].getBackground() != Color.BLUE && inputW <= inputS) {
	  input = 's'; }
	 else if (posizioneTestaY != 20 && griglia[posizioneTestaX][posizioneTestaY+1].getBackground() != Color.BLUE) {
	  input = 'd'; }
	 else if (posizioneTestaY != 0 && griglia[posizioneTestaX][posizioneTestaY-1].getBackground() != Color.BLUE) {
	  input = 'a'; }
     else if (posizioneTestaX != 0 && griglia[posizioneTestaX-1][posizioneTestaY].getBackground() != Color.BLUE && posizioneFruttoX < posizioneTestaX) {
	  input = 'w'; }
	 else if (posizioneTestaX != 10 && griglia[posizioneTestaX+1][posizioneTestaY].getBackground() != Color.BLUE && posizioneFruttoX > posizioneTestaX) {
	  input = 's'; }
	 else if (posizioneTestaX != 0 && griglia[posizioneTestaX-1][posizioneTestaY].getBackground() != Color.BLUE) {
	  input = 'w'; }
	 else if (posizioneTestaX != 10 && griglia[posizioneTestaX+1][posizioneTestaY].getBackground() != Color.BLUE) {
	  input = 's'; }
	 else {
	  input = 'a'; } } } }
	  
  else if (flagLoop == true || contatore <= 3) { // Tipo AI loop
   if (posizioneFruttoY > posizioneTestaY) { // Calcola l'input se il frutto si trova piu' a destra del serpente
    if (posizioneTestaY != 20 && griglia[posizioneTestaX][posizioneTestaY+1].getBackground() != Color.BLUE) {
     input = 'd'; }
    else {
     if (posizioneTestaX != 0 && griglia[posizioneTestaX-1][posizioneTestaY].getBackground() != Color.BLUE) {
	  input = 'w'; }
	 else if (posizioneTestaX != 10 && griglia[posizioneTestaX+1][posizioneTestaY].getBackground() != Color.BLUE) {
	  input = 's'; }
	 else {
	  input = 'a'; } } }
  
   else if (posizioneFruttoY < posizioneTestaY) { // Calcola l'input se il frutto si trova piu' a sinistra del serpente
    if (posizioneTestaY != 0 && griglia[posizioneTestaX][posizioneTestaY-1].getBackground() != Color.BLUE) {
     input = 'a'; }
    else {
     if (posizioneTestaX != 0 && griglia[posizioneTestaX-1][posizioneTestaY].getBackground() != Color.BLUE) {
	  input = 'w'; }
	 else if (posizioneTestaX != 10 && griglia[posizioneTestaX+1][posizioneTestaY].getBackground() != Color.BLUE) {
	  input = 's'; }
	 else {
	  input = 'd'; } } }
	 
   else { // Calcola l'input se il frutto si trova alla stessa coordinata Y del serpente
    if (posizioneFruttoX > posizioneTestaX) { // Calcola l'input se il frutto si trova piu' in basso
     if (posizioneTestaX != 10 && griglia[posizioneTestaX+1][posizioneTestaY].getBackground() != Color.BLUE) {
      input = 's'; }
	 else {
	  if (posizioneTestaY != 20 && griglia[posizioneTestaX][posizioneTestaY+1].getBackground() != Color.BLUE) {
	   input = 'd'; }
	  else if (posizioneTestaY != 0 && griglia[posizioneTestaX][posizioneTestaY-1].getBackground() != Color.BLUE) {
	   input = 'a'; }
	  else {
	   input = 'w'; } } }
	  
    else { // Calcola l'input se il frutto si trova piu' in alto
     if (posizioneTestaX != 0 && griglia[posizioneTestaX-1][posizioneTestaY].getBackground() != Color.BLUE) {
      input = 'w'; }
	 else {
	  if (posizioneTestaY != 20 && griglia[posizioneTestaX][posizioneTestaY+1].getBackground() != Color.BLUE) {
	   input = 'd'; }
	  else if (posizioneTestaY != 0 && griglia[posizioneTestaX][posizioneTestaY-1].getBackground() != Color.BLUE) {
	   input = 'a'; }
	  else {
	   input = 's'; } } } } }
	
  contatoreSwap = contatoreSwap + 1; 
  if (contatoreSwap == 1 && swap == true) { // Cambia il tipo di IA
   swap = !swap;
   contatoreSwap = 0; }
  else if (contatoreSwap == 1 && swap == false) {
   swap = !swap;
   contatoreSwap = 0; }
  System.out.println(swap + " " + input + " ["+inputW+","+inputD+","+inputS+","+inputA+"]"); }
    
 public boolean posizioneCorretta(int x, int y) { // Controlla se la nuova posizione della testa del serpente è corretta! (Metodo ausiliario di "muoviSnake")
  try {
   if (griglia[x][y].getBackground() == Color.BLUE) {
    return false; }
   else {
    return true; } }
  catch(ArrayIndexOutOfBoundsException e) {
   return false; } }
 
 public boolean muoviSnake() { // Muove il serpente a seconda della posizione scelta. Ritorna true se il movimento è possibile. 
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
	if (posizioneCorretta(ultimoX + aggiungiX, ultimoY + aggiungiY) == false) { // Se il serpente si scontra contro qualcosa il gioco finisce
	 return false; }
    correnti.setX(ultimoX+aggiungiX); // La testa si sposta e assume nuove coordinate
    correnti.setY(ultimoY+aggiungiY);
	if (griglia[correnti.getX()][correnti.getY()].getBackground() == Color.RED) { // Il serpente mangia, viene aggiornato il punteggio e un altro frutto viene aggiunto
	 inserisciFrutto();
	 aggiornaPunteggio();
     mangiato = true; }
    griglia[correnti.getX()][correnti.getY()].setBackground(Color.BLUE);
	posizioneTestaX = correnti.getX();
	posizioneTestaY = correnti.getY();
    testa = false;	}
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
  timerLoop.cancel(); // Resetta il timer per i loop
  if (flagLoop == true) {
   flagLoop = false; }
  timerLoop = new java.util.Timer();
  if (benchmark == false) {
   timerLoop.schedule(new loop(), 10000, 10000); }
  else {
   timerLoop.scheduleAtFixedRate(new loop(), 1000, 1000); } 
  contatore = contatore + 1;
  if (contatore > contatoreRecord) {
   contatoreRecord = contatore;  }
  punteggio.setText("Punteggio dell'IA: " + contatore);
  if (benchmark == false) {
   record.setText("Record dell'IA: " + contatoreRecord); }
  else {
   if (numeroPartite != 1) {
    record.setText("Record dell'IA: " + contatoreRecord + "        Media dell'IA: " + sommaTotale/(numeroPartite-1)); }
   else {
    record.setText("Record dell'IA: " + contatoreRecord + "        Media dell'IA: 0"); } } }
 
 public void gameOver() { // Chiede al giocatore se tornare al menu o iniziare una nuovo test
  if (benchmark == false) {
   griglia[posizioneTestaX][posizioneTestaY].setBackground(Color.BLACK);
   String[] opzioni = {"Nuova Partita", "Menu"};
   int scelta = JOptionPane.showOptionDialog(null, new JLabel("L'IA ha fatto Game Over!", JLabel.CENTER), "SNAKE - Test IA Normale", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, opzioni, opzioni[0]);
   if (scelta == 0) {
    inizioNuovoTest();	}
   else {
    apriMenu();  } }
  else {
   inizioNuovoBenchmark(); } }
  
 public int calcolaBlocchiBianchi(char c) { // Conta il numero di spazi bianchi dalla testa del serpente fino alla fine della griglia per scegliere una direzione migliore
  boolean flag = true; // Permette il controllo delle le caselle della direzione di c 
  boolean flag2 = true; // Permette il controllo delle caselle adiacenti a quella attuale
  boolean flag3 = true; // Permette il controllo delle caselle adiacenti a quella attuale
  int spaziBianchi = 0; // Numero di spazi bianchi incontrati
  int puntiFrutto = 100; // Punteggio bonus da assegnare se si incontra un frutto durante la conta di spazi bianchi
  try {
   //////
   if (c == 'w') { // Controlla lo spazio se il serpente si deve muove in alto
    for (int i = posizioneTestaX - 1; i >= 0; i--) { // Scorre la griglia finche' non finisce sul bordo
	 if (flag == true) {
	  if (griglia[i][posizioneTestaY].getBackground() != Color.BLUE && i != 0) { // Controlla se la casella e' vuota
	   if (griglia[i][posizioneTestaY].getBackground() == Color.WHITE) { // Viene incontrato uno spazio bianco
	    spaziBianchi = spaziBianchi + 1; }
	   else { // Viene incontrato un frutto: vengono aggiunti punti bonus alla conta!
	    spaziBianchi = spaziBianchi + puntiFrutto; } }
	   
	  else if (griglia[i][posizioneTestaY].getBackground() != Color.BLUE && i == 0) { // Controlla se e' arrivato alla fine della griglia, dopodiche' comincia a sondare le zone adiacenti
	   flag = false;
	   if (griglia[i][posizioneTestaY].getBackground() == Color.WHITE) {
	    spaziBianchi = spaziBianchi + 1; }
	   else {
	    spaziBianchi = spaziBianchi + puntiFrutto; }
	   for (int j = posizioneTestaY-1; j >= 0; j--) { // Sonda la zona a sinistra
	    if (flag2 == true) {
		 if (griglia[i][j].getBackground() != Color.BLUE) { 
		  if (griglia[i][j].getBackground() == Color.WHITE) {
		   spaziBianchi = spaziBianchi + 1; }
		  else {
		   spaziBianchi = spaziBianchi + puntiFrutto; } }
		 else { 
		  flag2 = false; } } }
	   for (int j = posizioneTestaY+1; j < 21; j++) { // Controlla la zona a destra
	    if (flag3 == true) {
		 if (griglia[i][j].getBackground() != Color.BLUE) {
		  if (griglia[i][j].getBackground() == Color.WHITE) {
		   spaziBianchi = spaziBianchi + 1; }
		  else {
		   spaziBianchi = spaziBianchi + puntiFrutto; } }
		 else {
		  flag3 = false; } } } }
	   
	  else if (griglia[i][posizioneTestaY].getBackground() == Color.BLUE) { // Se trova un blocco blu si ferma per controllare le zone adiacenti
	   flag = false;
	   if (spaziBianchi != 0) {
	    for (int j = posizioneTestaY-1; j >= 0; j--) {
	     if (flag2 == true) {
		  if (griglia[i+1][j].getBackground() != Color.BLUE) { 
		   if (griglia[i+1][j].getBackground() == Color.WHITE) {
		    spaziBianchi = spaziBianchi + 1; }
		   else {
		    spaziBianchi = spaziBianchi + puntiFrutto; } }
	      else {
		   flag2 = false; } } }
	    for (int j = posizioneTestaY+1; j < 21; j++) {
	     if (flag3 == true) {
		  if (griglia[i+1][j].getBackground() != Color.BLUE) {
		   if (griglia[i+1][j].getBackground() == Color.WHITE) {
		    spaziBianchi = spaziBianchi + 1; }
		   else {
		    spaziBianchi = spaziBianchi + puntiFrutto; } }
		  else {
		   flag3 = false; } } } } } } } }

   //////	
   else if (c == 's') {
    for (int i = posizioneTestaX+1; i < 11; i++) {
	 if (flag == true) {
	  if (griglia[i][posizioneTestaY].getBackground() != Color.BLUE && i != 10) { 
	   if (griglia[i][posizioneTestaY].getBackground() == Color.WHITE) {
		spaziBianchi = spaziBianchi + 1; }
	   else {
		spaziBianchi = spaziBianchi + puntiFrutto; } }
	   
	  else if (griglia[i][posizioneTestaY].getBackground() != Color.BLUE && i == 10) { 
	   flag = false;
	   if (griglia[i][posizioneTestaY].getBackground() == Color.WHITE) {
	    spaziBianchi = spaziBianchi + 1; }
	   else {
	    spaziBianchi = spaziBianchi + puntiFrutto; }
	   for (int j = posizioneTestaY-1; j >= 0; j--) { 
	    if (flag2 == true) {
		 if (griglia[i][j].getBackground() != Color.BLUE) { 
		  if (griglia[i][j].getBackground() == Color.WHITE) {
		   spaziBianchi = spaziBianchi + 1; }
		  else {
		   spaziBianchi = spaziBianchi + puntiFrutto; } }
		 else { 
		  flag2 = false; } } }
	   for (int j = posizioneTestaY+1; j < 21; j++) { 
	    if (flag3 == true) {
		 if (griglia[i][j].getBackground() != Color.BLUE) {
		  if (griglia[i][j].getBackground() == Color.WHITE) {
		    spaziBianchi = spaziBianchi + 1; }
		   else {
		    spaziBianchi = spaziBianchi + puntiFrutto; } }
		 else {
		  flag3 = false; } } } }
	   
	  else if (griglia[i][posizioneTestaY].getBackground() == Color.BLUE) { 
	   flag = false;
	   if (spaziBianchi != 0) {
	    for (int j = posizioneTestaY-1; j >= 0; j--) {
	     if (flag2 == true) {
		  if (griglia[i-1][j].getBackground() != Color.BLUE) { 
		   if (griglia[i-1][j].getBackground() == Color.WHITE) {
		    spaziBianchi = spaziBianchi + 1; }
		   else {
		    spaziBianchi = spaziBianchi + puntiFrutto; } }
		  else {
		   flag2 = false; } } }
	    for (int j = posizioneTestaY+1; j < 21; j++) {
	     if (flag3 == true) {
		  if (griglia[i-1][j].getBackground() != Color.BLUE) {
		   if (griglia[i-1][j].getBackground() == Color.WHITE) {
		    spaziBianchi = spaziBianchi + 1; }
		   else {
		    spaziBianchi = spaziBianchi + puntiFrutto; } }
		  else {
		   flag3 = false; } } } } } } } }
   
   //////   
   else if (c == 'a') {
	for (int i = posizioneTestaY-1; i >= 0; i--) {
	 if (flag == true) {
	  if (griglia[posizioneTestaX][i].getBackground() != Color.BLUE && i != 0) {
	   if (griglia[posizioneTestaX][i].getBackground() == Color.WHITE) {
		spaziBianchi = spaziBianchi + 1; }
	   else {
		spaziBianchi = spaziBianchi + puntiFrutto; } }
	  
	  else if (griglia[posizioneTestaX][i].getBackground() != Color.BLUE && i == 0) {
	   flag = false;
	   if (griglia[posizioneTestaX][i].getBackground() == Color.WHITE) {
	    spaziBianchi = spaziBianchi + 1; }
	   else {
	    spaziBianchi = spaziBianchi + puntiFrutto; }
	   for (int j = posizioneTestaX-1; j >= 0; j--) {
	    if (flag2 == true) {
		 if (griglia[j][i].getBackground() != Color.BLUE) { 
		  if (griglia[j][i].getBackground() == Color.WHITE) {
		    spaziBianchi = spaziBianchi + 1; }
		   else {
		    spaziBianchi = spaziBianchi + puntiFrutto; } }
		 else { 
		  flag2 = false; } } }
	   for (int j = posizioneTestaX+1; j < 11; j++) { 
	    if (flag3 == true) {
		 if (griglia[j][i].getBackground() != Color.BLUE) {
		  if (griglia[j][i].getBackground() == Color.WHITE) {
		   spaziBianchi = spaziBianchi + 1; }
		  else {
		   spaziBianchi = spaziBianchi + puntiFrutto; } }
		 else {
		  flag3 = false; } } } }
	    
	  else if (griglia[posizioneTestaX][i].getBackground() == Color.BLUE) { 
	   flag = false;
	   if (spaziBianchi != 0) {
	   for (int j = posizioneTestaX-1; j >= 0; j--) {
	    if (flag2 == true) {
		 if (griglia[j][i+1].getBackground() != Color.BLUE) { 
		  if (griglia[j][i+1].getBackground() == Color.WHITE) {
		   spaziBianchi = spaziBianchi + 1; }
		  else {
		   spaziBianchi = spaziBianchi + puntiFrutto; } }
		  else { 
		   flag2 = false; } } }
	    for (int j = posizioneTestaX+1; j < 11; j++) {
	     if (flag3 == true) {
		  if (griglia[j][i+1].getBackground() != Color.BLUE) {
		   if (griglia[j][i+1].getBackground() == Color.WHITE) {
		    spaziBianchi = spaziBianchi + 1; }
		   else {
		    spaziBianchi = spaziBianchi + puntiFrutto; } }
		  else {
		   flag3 = false; } } } } } } } }
		   
  //////  
  else if (c == 'd') {
	for (int i = posizioneTestaY+1; i < 21; i++) {
	 if (flag == true) {
	  if (griglia[posizioneTestaX][i].getBackground() != Color.BLUE && i != 20) {
	   if (griglia[posizioneTestaX][i].getBackground() == Color.WHITE) {
		spaziBianchi = spaziBianchi + 1; }
	   else {
		spaziBianchi = spaziBianchi + puntiFrutto; } }
	  
	  else if (griglia[posizioneTestaX][i].getBackground() != Color.BLUE && i == 20) {
	   flag = false;
	   if (griglia[posizioneTestaX][i].getBackground() == Color.WHITE) {
	    spaziBianchi = spaziBianchi + 1; }
	   else {
	    spaziBianchi = spaziBianchi + puntiFrutto; }
	   for (int j = posizioneTestaX-1; j >= 0; j--) {
	    if (flag2 == true) {
		 if (griglia[j][i].getBackground() != Color.BLUE) { 
		  if (griglia[j][i].getBackground() == Color.WHITE) {
		   spaziBianchi = spaziBianchi + 1; }
		  else {
		   spaziBianchi = spaziBianchi + puntiFrutto; } }
		 else { 
		  flag2 = false; } } }
	   for (int j = posizioneTestaX+1; j < 11; j++) {
	    if (flag3 == true) {
		 if (griglia[j][i].getBackground() != Color.BLUE) {
		  if (griglia[j][i].getBackground() == Color.WHITE) {
		   spaziBianchi = spaziBianchi + 1; }
		  else {
		   spaziBianchi = spaziBianchi + puntiFrutto; } }
		 else {
		  flag3 = false; } } } }
	    
	  else if (griglia[posizioneTestaX][i].getBackground() == Color.BLUE) { 
	   flag = false;
	   if (spaziBianchi != 0) {
	   for (int j = posizioneTestaX-1; j >= 0; j--) {
	    if (flag2 == true) {
		 if (griglia[j][i-1].getBackground() != Color.BLUE) { 
		  if (griglia[j][i-1].getBackground() == Color.WHITE) {
		   spaziBianchi = spaziBianchi + 1; }
		  else {
		   spaziBianchi = spaziBianchi + puntiFrutto; } }
		  else { 
		   flag2 = false; } } }
	    for (int j = posizioneTestaX+1; j < 11; j++) {
	     if (flag3 == true) {
		  if (griglia[j][i-1].getBackground() != Color.BLUE) {
		   if (griglia[j][i-1].getBackground() == Color.WHITE) {
		    spaziBianchi = spaziBianchi + 1; }
		   else {
		    spaziBianchi = spaziBianchi + puntiFrutto; } }
		  else {
		   flag3 = false; } } } } } } } }
  return spaziBianchi;	}
   
  catch(ArrayIndexOutOfBoundsException e) { 
   return spaziBianchi; } }
 
 public void apriMenu() { // Torna al menu
  Menu menu = new Menu(overheadMenu, contatoreRecord);
  menu.setSize(510,290);
  menu.setVisible(true);
  menu.setResizable(false);
  menu.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
  setVisible(false);
  dispose();  }
 
 public void inizioNuovoTest() { // Inizia un nuovo test
  IASnake nuovaPartita = new IASnake(contatoreRecord, overheadMenu, numeroPartite+1, sommaTotale+contatore, false);
  nuovaPartita.setSize(435, 296);
  nuovaPartita.setResizable(false);
  nuovaPartita.setVisible(true);
  nuovaPartita.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
  setVisible(false);
  dispose(); }
 
 public void inizioNuovoBenchmark() { // Inizia un nuovo test in modalita' benchmark
  IASnake nuovaPartita = new IASnake(contatoreRecord, overheadMenu, numeroPartite+1, sommaTotale+contatore, true);
  nuovaPartita.setSize(435, 296); 
  nuovaPartita.setResizable(false);
  nuovaPartita.setVisible(true);
  nuovaPartita.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
  setVisible(false);
  dispose(); } }

  