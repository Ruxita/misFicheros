package misFicheros;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class GuiBuscador extends JFrame {

    private JTextField txtExtension;
    private JButton btnBuscar;
    private JTextArea areaResultados;
    private BuscarArchivo buscador; // instancia del buscador

    // constructor: configurar ventana y componentes
    public GuiBuscador() {
	setTitle("Buscador de Archivos");
	setSize(700, 500);
	setDefaultCloseOperation(EXIT_ON_CLOSE);
	setLayout(new BorderLayout());

	buscador = new BuscarArchivo(); // inicializar el objeto buscador

	// Panel superior con campo de texto y botón
	JPanel panelSuperior = new JPanel(new FlowLayout());
	panelSuperior.add(new JLabel("Extension:"));
	txtExtension = new JTextField(10);
	btnBuscar = new JButton("Buscar");
	panelSuperior.add(txtExtension);
	panelSuperior.add(btnBuscar);
	add(panelSuperior, BorderLayout.NORTH);

	// area de texto para mostrar los resultados
	areaResultados = new JTextArea();
	areaResultados.setEditable(false);
	add(new JScrollPane(areaResultados), BorderLayout.CENTER);

	// Accion botón "Buscar"
	btnBuscar.addActionListener(e -> buscarArchivos());

	// Doble clic para ejecutar .exe
	areaResultados.addMouseListener(new MouseAdapter() {
	    @Override
	    public void mouseClicked(MouseEvent e) {
		if (e.getClickCount() == 2) {
		    try {
			int pos = areaResultados.getCaretPosition();
			int linea = areaResultados.getLineOfOffset(pos);
			int inicio = areaResultados.getLineStartOffset(linea);
			int fin = areaResultados.getLineEndOffset(linea);
			String archivo = areaResultados.getText().substring(inicio, fin).trim();

			// si el archivo es un .exe, se ejecuta
			if (archivo.toLowerCase().endsWith(".exe")) {
			    ProcessBuilder pb = new ProcessBuilder(archivo);
			    pb.start();
			    JOptionPane.showMessageDialog(null, "Ejecutando: " + archivo);
			} else {
			    JOptionPane.showMessageDialog(null, "Solo se pueden ejecutar archivos .exe");
			}
		    } catch (Exception ex) {
			JOptionPane.showMessageDialog(null, "Error al ejecutar el archivo: " + ex.getMessage());
		    }
		}
	    }
	});
    }

    // metodo gestion busqueda archivos con la extensión introducida
    private void buscarArchivos() {
	String extension = txtExtension.getText().trim();

	// validar que se ha introducido la extensión
	if (extension.isEmpty()) {
	    JOptionPane.showMessageDialog(this, "Introduce una extension, por ejemplo: exe o txt");
	    return;
	}
	// Asegurar que la extension comience con un punto
	if (!extension.startsWith(".")) {
	    extension = "." + extension;
	}
	final String extFinal = extension;

	areaResultados.setText("Buscando archivos " + extension + "...\n");

	// ejecutar la busqueda en segundo plano sin bloquear la interfaz
	SwingWorker<List<String>, Void> worker = new SwingWorker<List<String>, Void>() {
	    @Override
	    protected List<String> doInBackground() {
		// llama al metod de busqueda de la clase BuscarArchivo
		return buscador.buscar(extFinal);
	    }

	    @Override
	    protected void done() {
		try {
		    List<String> resultados = get();
		    areaResultados.setText("");
		    if (resultados.isEmpty()) {
			areaResultados.append("No se encontraron archivos con la extensión " + extFinal + ".\n");
		    } else {
			for (String r : resultados) {
			    areaResultados.append(r + "\n");
			}
		    }
		} catch (Exception e) {
		    areaResultados.append("Error: " + e.getMessage());
		}
	    }
	};

	worker.execute();
    }

    public static void main(String[] args) {
	SwingUtilities.invokeLater(() -> new GuiBuscador().setVisible(true));
    }
}