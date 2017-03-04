import java.util.ArrayList;

public class SchroedingerIntegration {

	public static double h = 6.626070040*Math.pow(10, -34); // wirkungsquantum
	public static double u = 9.10938356*Math.pow(10,-31); //elementarmasse
	public static double e = 1.6021766208*Math.pow(10,-19); //elementarladung

	public static double e0 = 8.85418781762*Math.pow(10,-12);

	private Game g;
	private Thread t1;

	public SchroedingerIntegration(Game g) {
		this.g = g;
	}
	
	
	public void run() throws InterruptedException{
		int energylevels = 5;
		int xsize = g.width*3/8;
		int ysize = g.height*3/8;
		//erstellen der koordinatensysteme
		int anzahlks = 2;
		for(int i = 0; i < anzahlks; i++ ){
			CoordinateSystem k = new CoordinateSystem(g,g.width/2*i + g.width*1/16, g.height/2 - ysize/2, xsize, ysize);
			g.ks.add(k);
			k.drawpoints = false;
			if(true){// growing range only in left coordinate system
				k.growingrange = true;
			}
			k.plotThickness = 1;
			g.calcTime = 50;
			if(i == anzahlks -1){
				k.headline = "Energieniveaus";
			}else{
				k.headline = "Suche nach Energieniveaus";
			}
			k.xlabel = "Abstand des Kerns in m";
			k.ylabel = "Energie in eV";
			Funktion coulomb = new CoulombFunktion(k,0,0,false); 
			k.funktions.add(coulomb);
		}

		//numerische integration
		Energieeigenwerte E = new Energieeigenwerte(new Coulomb(), -16*e, -0.1*e);
		for(int i = 0; i < energylevels; i++){
			E.step();
			
			ArrayList<ArrayList<ArrayList<ArrayList<Double>>>> l = E.gibloesungsschritte();
			//for(int s = 0; s < l.size(); s++){

					//g.ks.get(0).addEnergy(E.getEnergy()/e);	
					g.ks.get(0).simulation.add(l.get(0));
			//}
			System.out.println(E.getEnergy()/e);
			g.ks.get(1).addEnergy(E.getEnergy()/e);	
			g.ks.get(1).solution.add(E.getSolution());
			
			
			t1 = new Thread(){
				public void run(){
					try {
						while(!g.simulated&&!isInterrupted()){
							g.repaint();
							Thread.sleep(100); // sleeping time
						}
					} catch (InterruptedException e) {
					}
				}
			};
			t1.start();
		}
	}


	public void stop() {
		t1.interrupt();
		t1 = null;
	}

}
