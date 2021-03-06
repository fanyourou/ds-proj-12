import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class SilentSimulationRun {
	
	public static void main(String[] args) {
		Locale.setDefault(Locale.US);
		StringBuilder str = new StringBuilder("");
		StringBuilder cars = new StringBuilder("");
		
		Kattio io = new Kattio(System.in, System.out);
		int lines = io.getInt();
		for (int i = 0; i < lines; i++) 
			str.append(io.readLine()+"\n");
		lines = io.getInt();
		for (int i = 0; i < lines; i++) 
			cars.append(io.readLine()+"\n");
		
		RoutingAlgorithm[] algos = new RoutingAlgorithm[] { 
				new RandomRouting(),
				new SimpleRouting(),
				new ADPP(),
				new ADPPSmart(),
				new LocalShortestPathRouting(),
				new LocalShortestPathRoutingWithTrafficLights(),
				new LocalShortestPathRoutingWithTrafficLightsAndReservation(),
				new HeatMapRouting(),
				new ADPPKLocal(3, 1), // depth 3, 1 car hop
				new ADPPSmartKLocal(3, 1)
		};
		
		/* measure algorithms */
		
		for (RoutingAlgorithm r : algos) {
			ManhattenLayout ml = null;
			try {
				ml = new ManhattenLayout(str.toString(), cars.toString(), r);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			Simulation sim = ml.getSimulation();
			int upperlimit = 20000;
			int i = 0;
			for (i = 0; !sim.isFinished() && i < upperlimit; i++)
				sim.progress();

			System.out.printf("[%s]\n", r.getClass().getName());
			if (i == upperlimit)
				System.out.println(" never finished");
			else
				System.out.printf(" time to finish: %d\n", i);
			/* car stats */
			ArrayList<Integer> carStats = sim.getVehicleFinishTimes();
			Collections.sort(carStats);
			if(carStats.size() > 0)
			System.out.printf(" car stats:\n  min time: %d\n  max time: %d\n  avg time: %f\n  median: %f\n",
				carStats.get(0), carStats.get(carStats.size() - 1), calculateAverage(carStats), calculateMedian(carStats));
		}
		io.close();
	}
	


	public static double calculateAverage (List<Integer> values) {
		int sum = 0;
		for (int v : values)
			sum += v;
		return (double)sum / (double)values.size();
	}

	public static double calculateMedian (ArrayList<Integer> values)
	{
		if (values.size() % 2 == 1)
			return values.get((values.size()+1)/2-1);
		else
			return (values.get(values.size()/2-1) + values.get(values.size()/2)) / 2.0;
	}
}
