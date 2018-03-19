package ProyectoMovil.Automatizacion.Utilitario;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HiloTiempo extends Thread {

	public int tiempoEjecucion;
	private Logger logger;
	private String device;

	public HiloTiempo(String device) {

		super(device);
		
		this.start();
		logger = LoggerFactory.getLogger(HiloTiempo.class);
		tiempoEjecucion = 0;
		this.device = device;
	}

	@Override
	public void run() {

		try {

			while (true) {

				tiempoEjecucion++;
				TimeUnit.SECONDS.sleep(1);

			}
			
		} catch (Exception e) {
			e.printStackTrace();
			// System.out.println("Error en el HILO:\n");
			//JOptionPane.showMessageDialog(null, "El error en el hilo es: "+e.getMessage() +" para el device:\n"+device);

			logger.info("\n----------------------------------------------------------------------\n");
			logger.info("-----" + device + "---- HiloTiempo -----\n");
			logger.error("NE1: " +e.toString());


		}

	}

}
