package misFicheros;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class BuscarArchivo {

    public List<String> buscar(String extension) {
	// lista donde se guardan los resultados de la búsqueda
	List<String> resultados = new ArrayList<>();

	try {
	    // Asegurar que la extensión comience por "."
	    if (!extension.startsWith(".")) {
		extension = "." + extension;
	    }

	    // Obtener las unidades del sistema (C:\, D:\, etc.)
	    String[] unidades = { "C:", "D:", "E:" };

	    // recorrer las unidades del sistema
	    for (String unidad : unidades) {
		File unidadFile = new File(unidad + "\\");

		// Saltar si la unidad no existe
		if (!unidadFile.exists()) {
		    continue;
		}
		System.out.println("Buscando en " + unidad + "...");

		// Crea un proceso del sistema que ejecute el comando "dir"
		ProcessBuilder pb = new ProcessBuilder("cmd", "/c", "dir", unidad + "\\*" + extension, "/s", "/b");
		Process proceso = pb.start();

		// Leer la salida del proceso
		BufferedReader br = new BufferedReader(new InputStreamReader(proceso.getInputStream()));
		String linea;
		while ((linea = br.readLine()) != null) {
		    linea = linea.trim(); // limpia espacios y saltos de línea invisibles
		    if (!linea.isEmpty()) { // evita agregar líneas vacías
			resultados.add(linea);
		    }
		}

		// Esperar a que termine
		int exitCode = proceso.waitFor();
		System.out.println("Búsqueda en " + unidad + " finalizada con código: " + exitCode);
	    }

	} catch (Exception e) {
	    System.out.println("Error al buscar archivos: " + e.getMessage());
	}

	return resultados;
    }
}
