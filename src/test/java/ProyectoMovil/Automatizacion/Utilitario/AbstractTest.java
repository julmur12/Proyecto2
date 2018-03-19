package ProyectoMovil.Automatizacion.Utilitario;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import ProyectoMovil.Automatizacion.app.MutualMovil;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.remote.MobileCapabilityType;

@SuppressWarnings("rawtypes")
public abstract class AbstractTest {

	// private static final String STF_SERVICE_URL = "http://10.10.2.232:9090/";
	// private static final String ACCESS_TOKEN =
	// "ce8e9de5bc504d179d3f6667f2ef59af4f1533ec8c54451e998cb735932e27ff";
	// private static final String url = "http://10.10.1.143:4444/wd/hub";
	private static final String url = "http://localhost:4444/wd/hub";

	protected AppiumDriver driver;
	protected MutualMovil app;
	protected IntegracionDataBase conexionDB;
	public ClaseLog logger;

	protected SimpleDateFormat dateFormat;

	protected String fecha;

	// private Suite deviceApi;

	// @Parameters({ "UID" })
	// private boolean connectToStfDevice(String UID) throws
	// MalformedURLException, URISyntaxException {
	// STFService stfService = new STFService(STF_SERVICE_URL, ACCESS_TOKEN);
	// this.deviceApi = new Suite(stfService, UID);
	// return this.deviceApi.connectDevice();
	// }

	@Parameters({ "UID", "deviceName" })
	@BeforeClass
	public void setup(String UID, @Optional String deviceName) {

		// WINDOS
		// File appMutual = descargarApp();
		// LINUX
		// File appMutual = new File("/var/www/html/APP_AUTO/MutualApp.apk");

		dateFormat = new SimpleDateFormat("dd-MMM-yyyy_hh_mm");

		fecha = dateFormat.format(new Date());
		
		DesiredCapabilities capabilities = new DesiredCapabilities();

		if (UID.length() >= 35) {

			capabilities.setCapability(MobileCapabilityType.BROWSER_NAME, "MAC");
			capabilities.setCapability(MobileCapabilityType.PLATFORM_NAME, "MAC");
			capabilities.setCapability(MobileCapabilityType.DEVICE_NAME, deviceName);
			capabilities.setCapability(MobileCapabilityType.UDID, UID);
			capabilities.setCapability("automationName", "XCUITest");
			capabilities.setCapability("bundleId", "GrupoMutual.MobileApp");
			capabilities.setCapability("platformVersion", "10.0");
			capabilities.setCapability("wdaLocalPort", getPort());
			// capabilities.setCapability("webDriverAgentUrl", "http://localhost:8100");
			capabilities.setCapability("usePrebuiltWDA", true);
			capabilities.setCapability("useNewWDA", false);

			// capabilities.setCapability("bundleId", "AppDelegate");

			// capabilities.setCapability("clearSystemFiles", true);
			// capabilities.setCapability("fullReset", true);
			// capabilities.setCapability("autoAcceptAlerts", true);

		} else {

			capabilities.setCapability(MobileCapabilityType.BROWSER_NAME, "ANDROID");
			capabilities.setCapability(MobileCapabilityType.PLATFORM_NAME, "ANDROID");
			capabilities.setCapability(MobileCapabilityType.DEVICE_NAME, UID);
			capabilities.setCapability(MobileCapabilityType.UDID, UID);
			// capabilities.setCapability(MobileCapabilityType.APP, appMutual);
			capabilities.setCapability(MobileCapabilityType.APP_PACKAGE, "fi.cr.gmalv.mutualmovil.activities");
			capabilities.setCapability(MobileCapabilityType.APP_ACTIVITY, ".MenuActivity");
			// capabilities.setCapability("clearSystemFiles", true);
			// capabilities.setCapability("fullReset", true);
			// capabilities.setCapability("autoAcceptAlerts", true);

		}

		try {
			// if (connectToStfDevice(UID)) {
			//
			// driver = new AndroidDriver(new URL(url), capabilities);
			// driver.manage().timeouts().implicitlyWait(60, TimeUnit.SECONDS);
			//
			// app = new MutualMovil(driver, UID);
			//
			// } else {
			//
			// logger.info("\n----------------------------------------------------------------------\n");
			// logger.info("-----" + UID + "----ERROR _BEFORE_\n");
			// logger.error("N1: NO SE ENCUENTRA EL DISPOSITIVO EN EL STF O ESTA
			// CAIDO");
			// logger.info("----------------------------------------------------------------------");
			//
			// app.componentesGenerales().accionesUniversales().enviarLog("Error
			// Tecnico",
			// "NO SE ENCUENTRA EL DISPOSITVO", "BEFORE", "FALLO", fecha);
			//
			// }
			
			logger = new ClaseLog(UID, AbstractTest.class.getName());

			if (UID.length() >= 35) {

				driver = new IOSDriver(new URL(url), capabilities);

				app = new MutualMovil(driver, UID, logger);

				driver.manage().timeouts().implicitlyWait(60, TimeUnit.SECONDS);

			} else {

				driver = new AndroidDriver(new URL(url), capabilities);

				app = new MutualMovil(driver, UID, logger);

				driver.manage().timeouts().implicitlyWait(60, TimeUnit.SECONDS);
			}

			System.out.println("\nDEVICE: " + deviceName + " " + UID + " session: " + driver.getSessionId() + "\n");

			logger.error(
					"N1", "Error Esperado", "Se inicia el corrido SIN AUTORIZACION del STF " + "AUTORIZACION-"
							+ driver.getSessionId() + " Settings: " + driver.getSettings(),
					"Before", "Error _BEFORE_N1", fecha);

		} catch (Exception e) {
			e.printStackTrace();

			System.out.println("CAP CAP CAP" + capabilities);

			System.out.println("\nDEVICE: " + deviceName + " " + UID + " session: " + driver.getSessionId() + "\n");

			logger.error("NE1", "Error Tecnico", e.toString(), "Before", "Error _BEFORE_", fecha);

			// a this.deviceApi.releaseDevice();
			this.driver.quit();

		}
	}

	@Parameters({ "UID", "DEVICE", "USER", "DATA" })
	@AfterClass
	public void setOut(String UID, String DEVICE, @Optional String USER, String DATA) {

		logger.setLogger(AbstractTest.class.getName());

		
		try {
			if (DATA.equalsIgnoreCase("true")) {
				conexionDB = new IntegracionDataBase();

				logger.info("--->Dispositivo: " + DEVICE
						+ ", estable la conexion con la base de datos para el user id: " + USER + "<---\n");

				String delete = "delete ges_ta_usuario_app where dispositivo = '" + DEVICE
						+ "' and id_perfil_cliente = " + USER;

				conexionDB.deleteUpdate(delete);

				System.out.println("\nDEVICE: " + DEVICE + " " + UID + " session: " + driver.getSessionId() + "\n");

			} else {

				logger.info("--->Dispositivo: " + DEVICE
						+ " termina la ejecucion de sus pruebas sin ir a base de datos<---\n");

			}

			// a this.deviceApi.releaseDevice();
			this.driver.quit();

		} catch (Exception e) {

			// e.printStackTrace();
			System.out.println("\nDEVICE: " + DEVICE + " " + UID + " session: " + driver.getSessionId() + "\n");

			logger.error("NE2", "Error Tecnico", e.toString(), "Afther", "Error Afther", fecha);

			// a this.deviceApi.releaseDevice();
			this.driver.quit();
		}

	}

	// Para iOS sirve para poder correr cada dispositivo en diferente puerto y de
	// esta forma no se satura un unico puerto probocando falla
	public String getPort() {
		ServerSocket socket;
		String port = "";

		try {
			socket = new ServerSocket(0);
			socket.setReuseAddress(true);
			port = Integer.toString(socket.getLocalPort());
			socket.close();

		} catch (IOException e) {

			e.printStackTrace();
		}

		return port;
	}

	/*
	 * public File descargarApp() {
	 * 
	 * final File app = new File("MutualApp.apk");
	 * 
	 * SwingUtilities.invokeLater(new Runnable() { public void run() {
	 * 
	 * String url = "http://10.10.3.91/APP_AUTO/MutualApp.apk";
	 * 
	 * try {
	 * 
	 * URL descargar = new URL(url);
	 * 
	 * InputStream inputStream = descargar.openStream(); OutputStream outputStream =
	 * new FileOutputStream(app);
	 * 
	 * byte[] b = new byte[2048]; int longitud;
	 * 
	 * while ((longitud = inputStream.read(b)) != -1) { outputStream.write(b, 0,
	 * longitud); }
	 * 
	 * inputStream.close(); // Cerramos la conexión entrada outputStream.close(); //
	 * Cerramos la conexión salida } catch (MalformedURLException e) { // TODO
	 * Auto-generated catch block e.printStackTrace(); } catch (IOException e) { //
	 * TODO Auto-generated catch block e.printStackTrace(); }
	 * 
	 * } }); return app; }
	 */

}