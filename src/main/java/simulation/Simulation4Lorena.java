package simulation;

import java.util.ArrayList;
import java.util.List;

import model.Fleet;
import model.FuelPrice;
import model.Market;
import model.Port;
import model.PortNetwork;
import model.Ship;
import util.CAPEXCalculator;
import util.CSVwriter;

public class Simulation4Lorena extends Simulation{

	private String OUTPUT_DIR = "./data/";
	private String OUTPUT_DIR_ROOT = "./data/";
	private final String OUTPUT_CSV_FILE = "result.csv";
	
	private final String OUTPUT_ALL_RESULT = "result_all.csv";
	private final String OUTPUT_OVERALL_RESULT = "result_overall.csv";
	private CAPEXCalculator calcultaor;
	private boolean saveFlag;

	public Simulation4Lorena(int endTime, String outputDir, CAPEXCalculator calculator) {
		super(endTime);
		if(outputDir != null) this.OUTPUT_DIR = outputDir;
		this.calcultaor = calculator;
	}

	@Override
	public void save(int now) {
		List<String> outputList = new ArrayList<String>();
		List<Ship> ships = Fleet.getShips();
		if(now == -1){
			for(Ship ship : ships){
				outputList.add("");
				outputList.add("Status");
				outputList.add("Fuel Type");
				outputList.add("Cargo Type");
				outputList.add("Amount of Cargo");
				outputList.add("Amount of Fuel");
				outputList.add("Total Fuel Cost");
				outputList.add("Remaining Desitance");
				outputList.add("Berthing Port");
			}
			outputList.add("");
			outputList.add("Freight");
			outputList.add("HFO Price");
			outputList.add("LSFO Price");
			outputList.add("LNG Price");
			CSVwriter.write(OUTPUT_DIR + OUTPUT_CSV_FILE, outputList, false);
		}
		//TO-DO
		else{
			outputList.add(String.valueOf(now));
			for(Ship ship : ships){
				outputList.add(ship.getShipStatus().name());
				outputList.add(String.valueOf(ship.getFuelType()));
				outputList.add(String.valueOf(ship.getCargoType()));
				outputList.add(String.valueOf(ship.getAmountOfCargo()));
				outputList.add(String.valueOf(ship.getAmountOfFuel()));
				outputList.add(String.valueOf(ship.getTotalFuelPricise()));
				outputList.add(String.valueOf(ship.getRemainingDistance()));
				if(ship.getBerthingPort()!=null){
					outputList.add(ship.getBerthingPort().getName());
				}else{
					outputList.add("");
				}
				outputList.add("");
			}
			//this.portNetwork.save(now);
			double freight = Market.getFreight().get(0).getPrice();
			outputList.add(String.valueOf(freight));
			double lngPrice = 0;
			double hfoPrice = 0;
			double lsfoPrice = 0;
			for(FuelPrice fuel : Market.getFuels()){
				switch(fuel.getFuelType()){
				case HFO:
					hfoPrice = fuel.getPrice();
					break;
				case LNG:
					lngPrice = fuel.getPrice();
					break;
				case LSFO:
					lsfoPrice = fuel.getPrice();
				}
			}
			outputList.add(String.valueOf(hfoPrice));
			outputList.add(String.valueOf(lsfoPrice));
			outputList.add(String.valueOf(lngPrice));
			CSVwriter.write(OUTPUT_DIR + OUTPUT_CSV_FILE, outputList, true);
		}
		//System.out.println("Now: " + now + " Saved!");
	}

	@Override
	public void save() {
		List<String> outputList = new ArrayList<String>();
		List<String> outputListAll = new ArrayList<String>();
		List<Ship> ships = Fleet.getShips();
		List<Port> ports = PortNetwork.getPorts();
		double TTTM = 0;
		double TECU = 0;
		double totalSales = 0;
		double totalCo2 = 0;
		double totalNox = 0;
		double totalSox = 0;
		double totalWaitingTime = 0;
		outputList.add("Overall Result");

		CSVwriter.write(OUTPUT_DIR + OUTPUT_ALL_RESULT, outputList, false);

		outputList = new ArrayList<String>();
		double totalFuelCost = 0;
		for(Ship ship : ships){
			outputList.add("Total Cargo Amount");
			outputList.add("Total Transported Distance");
			outputList.add("Fuel Type");
			outputList.add("Total Fuel Cost");
			outputList.add("Total Cost");
			outputList.add("CO2");
			outputList.add("SOx");
			outputList.add("NOx");
			outputList.add("Waiting Time");
			outputList.add("");
			CSVwriter.write(OUTPUT_DIR + OUTPUT_ALL_RESULT, outputList, true);
	 		outputList = new ArrayList<String>();

			double totalCargo = ship.getTotalCargo();
			outputList.add(String.valueOf(totalCargo));
			double totalDistance = ship.getTotalDistance();
			outputList.add(String.valueOf(totalDistance));
			outputList.add(String.valueOf(ship.getFuelType()));
			double totalFuel = ship.getTotalFuelPricise();
			outputList.add(String.valueOf(totalFuel));
			double shipCost = ship.getTotalCost();
			double shipRevenue = ship.getCashFlow();
			outputList.add(String.valueOf(shipCost));
			double co2 = ship.getCo2();
			outputList.add(String.valueOf(co2));
			double sox = ship.getSox();
			outputList.add(String.valueOf(sox));
			double nox = ship.getNox();
			outputList.add(String.valueOf(nox));
			double waitingTime = ship.getWaitingTime();
			outputList.add(String.valueOf(waitingTime));
			outputList.add(String.valueOf(ship.getTotalTonKm()));
			outputList.add("");

			TTTM += ship.getTotalTonKm();
			if(totalCargo * totalDistance != 0) TECU += totalFuel / (totalCargo * totalDistance);
			totalFuelCost += ship.getTotalFuelPricise();
			totalSales += shipRevenue;
			totalCo2 += co2;
			totalSox += sox;
			totalNox += nox;
			totalWaitingTime += waitingTime;

			CSVwriter.write(OUTPUT_DIR + OUTPUT_ALL_RESULT, outputList, true);
			outputList = new ArrayList<String>();
		}
		for(Port port : ports){
			outputList.add("Port Name");
			outputList.add("Total Berthing Revenu");
			outputList.add("Total Bunkering Revenu");
			outputList.add("");
			CSVwriter.write(OUTPUT_DIR + OUTPUT_ALL_RESULT, outputList, true);
			outputList = new ArrayList<String>();

			outputList.add(port.getOperator().getName());
			double berthingRevenue = port.getOperator().getBerthingCash();
			double bunkeringRevenue = port.getOperator().getBunkeringCash();
			outputList.add(String.valueOf(berthingRevenue));
			outputList.add(String.valueOf(bunkeringRevenue));
			totalSales += berthingRevenue;
			totalSales += bunkeringRevenue;
			//totalFuelCost += bunkeringRevenue;
			outputList.add("");
			CSVwriter.write(OUTPUT_DIR + OUTPUT_ALL_RESULT, outputList, true);
	 		outputList = new ArrayList<String>();
		}
		
		outputList.add("System's ilities");
		outputList.add("Fuel efficiency");
		outputList.add("Total CO2 Emission rate");
		outputList.add("Total NOx Emission rate");
		outputList.add("Total SOx Emission rate");
		outputList.add("Total Waiting Time rate");
		outputList.add("InitialCost");
		CSVwriter.write(OUTPUT_DIR + OUTPUT_ALL_RESULT, outputList, true);
 		outputList = new ArrayList<String>();
		
		outputList.add("");
		System.out.println(String.valueOf(totalFuelCost));
		System.out.println(String.valueOf(TTTM));
		outputList.add(String.valueOf(totalFuelCost/TTTM));
		outputList.add(String.valueOf(totalCo2/TTTM));
		outputList.add(String.valueOf(totalNox/TTTM));
		outputList.add(String.valueOf(totalSox/TTTM));
		outputList.add((String.valueOf(totalWaitingTime/(this.getEndTime()-8760)/20*100)));
		outputList.add(String.valueOf(this.calcultaor.calculate()));
		outputListAll.addAll(outputList);
 		CSVwriter.write(OUTPUT_DIR + OUTPUT_ALL_RESULT, outputList, true);
 		CSVwriter.write(OUTPUT_DIR_ROOT + OUTPUT_OVERALL_RESULT, outputListAll, true);
		System.out.println("Overall Result Saved!");

	}
}
