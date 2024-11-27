import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.ArrayList;

public class CarreraDeAutos extends JPanel implements ActionListener, KeyListener {
    // Atributos del juego
    private int autoX = 200; 
    private int autoY = 400; 
    private int velocidad = 5; 
    private Timer timer; 
    private boolean juegoTerminado = false; 
    private int puntaje = 0; 

    // Imágenes del juego
    private Image autoImagen;
    private Image obstaculoImagen;
    private Image gameOverImagen;

    // Lista de obstáculos y líneas de la carretera
    private ArrayList<Obstaculo> obstaculos;
    private int[] lineasCarretera = {50, 150, 250, 350}; 
    private int desplazamientoCarretera = 0; 

    // Botón de reintentar para cuando el juego termine
    private JButton btnReintentar;

    public CarreraDeAutos() {
        // Configuración inicial del juego
        setFocusable(true); // Hacer que el panel sea capaz de recibir entradas del teclado
        addKeyListener(this); // Agregar un escuchador de teclado

        // Cargar imágenes del auto, obstáculos y pantalla de Game Over
        autoImagen = new ImageIcon(getClass().getResource("/auto.png")).getImage();
        obstaculoImagen = new ImageIcon(getClass().getResource("/obstaculo.png")).getImage();
        gameOverImagen = new ImageIcon(getClass().getResource("/gameover.jpg")).getImage();

        // Verificar si las imágenes se cargaron correctamente
        if (autoImagen == null || obstaculoImagen == null || gameOverImagen == null) {
            System.err.println("Error: No se pudieron cargar las imágenes. Verifica las rutas.");
            System.exit(1);
        }

        // Inicializar la lista de obstáculos
        obstaculos = new ArrayList<>();
        for (int i = 0; i < 3; i++) { 
            obstaculos.add(new Obstaculo((int) (Math.random() * 350), -(i * 200), velocidad));
        }

        // Crear y configurar el temporizador que actualiza el juego cada 30 ms
        timer = new Timer(30, this);

        // Crear el botón de reintentar (inicialmente invisible)
        btnReintentar = new JButton("Reintentar");
        btnReintentar.setBounds(150, 400, 100, 30); 
        btnReintentar.setVisible(false);
        btnReintentar.addActionListener(e -> reiniciarJuego()); 
        this.setLayout(null); 
        this.add(btnReintentar); 
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (!juegoTerminado) {
            // Dibujar fondo de la carretera
            g.setColor(Color.DARK_GRAY);
            g.fillRect(0, 0, 400, 500);

            // Dibujar las líneas divisorias de la carretera
            g.setColor(Color.WHITE);
            for (int linea : lineasCarretera) {
                for (int y = desplazamientoCarretera; y < 500; y += 100) {
                    g.fillRect(linea, y, 10, 50);
                }
            }

            // Dibujar el auto del jugador
            g.drawImage(autoImagen, autoX, autoY, 50, 100, this);

            // Dibujar los obstáculos
            for (Obstaculo obstaculo : obstaculos) {
                g.drawImage(obstaculoImagen, obstaculo.getX(), obstaculo.getY(), 50, 100, this);
            }

            // Mostrar el puntaje en la pantalla
            g.setColor(Color.WHITE);
            g.drawString("Puntaje: " + puntaje, 10, 20);
        } else {
            // Mostrar la pantalla de Game Over cuando el juego termine
            g.drawImage(gameOverImagen, 0, 0, 400, 500, this);
            g.setColor(Color.WHITE);
            g.drawString("¡Juego Terminado! Puntaje final: " + puntaje, 100, 250);

            // Hacer visible el botón de reintentar
            btnReintentar.setVisible(true);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!juegoTerminado) {
            // Actualizar el desplazamiento de las líneas de la carretera
            desplazamientoCarretera += velocidad;
            if (desplazamientoCarretera > 100) {
                desplazamientoCarretera = 0;
            }

            // Mover los obstáculos y detectar colisiones
            for (Obstaculo obstaculo : obstaculos) {
                obstaculo.mover();
                if (obstaculo.getY() > 500) {
                    obstaculo.reset(); // Si el obstáculo sale de la pantalla, lo reinicia
                    puntaje += 10; // Aumentar puntaje
                }

                // Detectar colisión con el auto
                if (obstaculo.getX() < autoX + 50 && obstaculo.getX() + 50 > autoX &&
                    obstaculo.getY() < autoY + 100 && obstaculo.getY() + 100 > autoY) {
                    juegoTerminado = true; 
                    timer.stop();
                }
            }

            repaint(); // Volver a pintar el panel
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        // Mover el auto con las teclas de dirección
        if (e.getKeyCode() == KeyEvent.VK_LEFT && autoX > 0) {
            autoX -= 10; 
        } else if (e.getKeyCode() == KeyEvent.VK_RIGHT && autoX < 350) {
            autoX += 10; 
        } else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
            velocidad -= 0.2; 
            for (Obstaculo obstaculo : obstaculos) {
                obstaculo.disminuirVelocidad(); // Reducir la velocidad de los obstáculos
            }
        } else if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            velocidad += 0.2; // Aumentar velocidad
            for (Obstaculo obstaculo : obstaculos) {
                obstaculo.incrementarVelocidad(); // Aumentar la velocidad de los obstáculos
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {}

    @Override
    public void keyTyped(KeyEvent e) {}

    public static void main(String[] args) {
        JFrame frame = new JFrame("NFS");

        // Mostrar el menú de opciones al inicio del juego
        String[] opciones = { "Jugar", "Créditos", "Salir" };
        int opcionSeleccionada = JOptionPane.showOptionDialog(frame, "Selecciona una opción", "Menú",
                JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, opciones, opciones[0]);

        if (opcionSeleccionada == 0) {
            CarreraDeAutos juego = new CarreraDeAutos();
            frame.add(juego);
            frame.setSize(400, 500);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setVisible(true);
            juego.startGame(); // Iniciar el juego
        } else if (opcionSeleccionada == 1) {
            // Mostrar los créditos
            JOptionPane.showMessageDialog(frame, "Rosa Maria Acevedo Rico \n Fernando Noe Macias Medina \n Alejandro Molina Medina \n Gisela Jazmin Gonzalez Romo \n Julio Cesar Ibarra Gonzalez", "Créditos", JOptionPane.INFORMATION_MESSAGE);
            System.exit(0);
        } else {
            System.exit(0); // Salir del juego
        }
    }

    // Método para iniciar el juego
    public void startGame() {
        juegoTerminado = false;
        puntaje = 0;
        velocidad = 5;
        autoX = 200;
        autoY = 400;

        obstaculos.clear(); // Limpiar la lista de obstáculos
        for (int i = 0; i < 3; i++) {
            obstaculos.add(new Obstaculo((int) (Math.random() * 350), -(i * 200), velocidad)); // Crear nuevos obstáculos
        }

        timer.start(); // Iniciar el temporizador
    }

    // Método para reiniciar el juego después de que ha terminado
    public void reiniciarJuego() {
        juegoTerminado = false;
        puntaje = 0;
        velocidad = 5;
        autoX = 200;
        autoY = 400;

        obstaculos.clear(); // Limpiar la lista de obstáculos
        for (int i = 0; i < 3; i++) {
            obstaculos.add(new Obstaculo((int) (Math.random() * 350), -(i * 200), velocidad)); // Crear nuevos obstáculos
        }

        btnReintentar.setVisible(false); // Ocultar el botón de reintentar
        startGame(); // Iniciar el juego
    }
}

class Obstaculo {
    private int x, y, velocidad;

    public Obstaculo(int x, int y, int velocidad) {
        this.x = x;
        this.y = y;
        this.velocidad = velocidad;
    }

    public void mover() {
        y += velocidad; // Mover el obstáculo hacia abajo
    }

    public void reset() {
        y = -100; // Restablecer la posición del obstáculo
        x = (int) (Math.random() * 350); // Cambiar la posición horizontal aleatoriamente
    }

    public void incrementarVelocidad() {
        velocidad += 1; // Aumentar la velocidad del obstáculo
    }

    public void disminuirVelocidad() {
        velocidad -= 1; // Reducir la velocidad del obstáculo
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
