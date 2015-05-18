package com.blogspot.rolandopalermo.sockets;

import java.awt.BorderLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.media.Buffer;
import javax.media.CaptureDeviceInfo;
import javax.media.CaptureDeviceManager;
import javax.media.Manager;
import javax.media.MediaLocator;
import javax.media.Player;
import javax.media.Processor;
import javax.media.control.FrameGrabbingControl;
import javax.media.format.VideoFormat;
import javax.media.util.BufferToImage;
import javax.swing.JOptionPane;
import javax.swing.Timer;

/**
 *
 * @author Rolando
 */
public class Acceder2Frames extends javax.swing.JFrame {

    //Atributos necesarios para acceder a una cámara web
    private Player player = null;
    private MediaLocator localizador = null;
    private Processor p;
    private String source = "vfw:Microsoft WDM Image Capture (Win32):0";
    private Timer timer;
    private Buffer buffer;
    private BufferToImage buffer_image = null;
    private String ip = "192.168.2.10";

    /** Creates new form Acceder2Frames */
    public Acceder2Frames(String host) {
        initComponents();
        //setDefaultLookAndFeelDecorated(true);

        servidorChat = host;
        localizador = new MediaLocator("vfw://0");
        timer = new Timer(1, new ActionListener() {   //Cada 1 milisegundo capturará el frame de video

            public void actionPerformed(ActionEvent e) {

                //verificar si el socket esta cerrado
//                if (!cliente.isConnected() || cliente.isClosed() || cliente.isOutputShutdown() || player.getVisualComponent() == null) {
//                    panelVideo.removeAll();
//                    panelVideo.updateUI();
//                    timer.stop();
//                    //aca va la pantala para transmisor
//
//                    dispose();
//                    return;
//                }


                FrameGrabbingControl fgc = (FrameGrabbingControl) player.getControl("javax.media.control.FrameGrabbingControl");
                buffer = fgc.grabFrame();
                if (buffer.getData() == null) {
                    return;
                }
                // Convert it to an image
                buffer_image = new BufferToImage((VideoFormat) buffer.getFormat());

                System.out.println("enviando");
                //panelVideo.repaint();
                BufferedImage bufferedImage = (BufferedImage) buffer_image.createImage(buffer);
                ByteArrayOutputStream salidaImagen = new ByteArrayOutputStream();
                try {
                    ImageIO.write(bufferedImage, "jpg", salidaImagen);
                    byte[] bytesImagen = salidaImagen.toByteArray();
                    salida.writeObject(bytesImagen);
                    salida.flush();
                    //                panelCaptura.setImage(img);
                } catch (Exception excepcionEOF) {
                    System.err.println("El cliente termino la conexión timer");
                    excepcionEOF.printStackTrace();

//                    JOptionPane.showMessageDialog(null, "error en timer \n\n"
//                            + excepcionEOF.getMessage() + "\n\n" + excepcionEOF.getCause());


                    /*conectado = false;
                    while (!conectado) {
                    try {

                     */ timer.stop();
                    ejecutarCliente();
                    /* } catch (IOException ex) {
                    Logger.getLogger(Acceder2Frames.class.getName()).log(Level.SEVERE, null, ex);
                    JOptionPane.showMessageDialog(null, "error en timer conectar servidor \n\n"
                    + ex.getMessage() + "\n\n" + ex.getCause());
                    conectado = false;
                    }
                    }*/
                    int i = 2;
                }
            }
        });
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public void iniciarCaptura() {
        try {
            player = Manager.createRealizedPlayer(localizador);
            player.start();
            if (player.getVisualComponent() != null) {
                panelVideo.add(player.getVisualComponent(), BorderLayout.CENTER);
                panelVideo.updateUI();
            }
        } catch (Exception e) {
            System.err.println(e.toString());
            JOptionPane.showMessageDialog(this, "error en iniciar captura \n\n"
                    + e.getMessage() + "\n\n" + e.getCause());
        }
    }

    public void acceso2Frames() {
        timer.start();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panelVideo = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Captura de Video - Transmisor de video");

        panelVideo.setLayout(new java.awt.BorderLayout());

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelVideo, javax.swing.GroupLayout.DEFAULT_SIZE, 629, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelVideo, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 507, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    public void ejecutarCliente() {


        try {
            conectarAServidor(); // Paso 1: crear un socket para realizar la conexión
            salida = new ObjectOutputStream(cliente.getOutputStream());
            salida.flush(); // vacíar búfer de salida para enviar información de encabezado
            acceso2Frames();
        } catch (Exception excepcionEOF) {
            System.err.println("El cliente termino la conexión en acceder2frames");
//            JOptionPane.showMessageDialog(this, "error en ejecutar cliente catch \n\n"
//                    + excepcionEOF.getMessage() + "\n\n" + excepcionEOF.getCause());
            timer.stop();
//            player.stop();
//            player = null;
//            localizador = null;
            ejecutarCliente();
//            new pantalla5().setVisible(true);
//            dispose();
        }
    } // fin del método ejecutarCliente

    // conectarse al servidor
    private void conectarAServidor() throws IOException {
        //InetAddress.getByName()
        //cliente = new Socket(servidorChat, 5000);
        cliente = new Socket(InetAddress.getByName(servidorChat), 5000);
        

    }

    private void cerrarConexion() {
        System.out.println("\nCerrando conexión");
        try {
            salida.close();
            cliente.close();
        } catch (IOException e) {
            System.out.println("error 187");
//            JOptionPane.showMessageDialog(this, "error en cerrar conexion \n\n"
//                    + e.getMessage() + "\n\n" + e.getCause());
        }
    }

    public static void main(String args[]) {
        setDefaultLookAndFeelDecorated(true);
        Acceder2Frames ventanita;
        ventanita = new Acceder2Frames("192.168.2.10");
        ventanita.iniciarCaptura();
        ventanita.ejecutarCliente();
    }
    private ObjectOutputStream salida;
    private String servidorChat;
    private Socket cliente;
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel panelVideo;
    // End of variables declaration//GEN-END:variables
}
