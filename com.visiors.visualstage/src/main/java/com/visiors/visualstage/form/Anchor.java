package com.visiors.visualstage.form;


public enum Anchor {

	North, 
	NorthEast, 
	East, 
	SouthEast, 
	South, 
	SouthWest, 
	West, 
	NorthWest, 
	Center;
	
//	public static Anchor toEnum(String str) {
//		if (North.name().equals(str))
//			return North;
//		if (NorthEast.name().equals(str))
//			return North;
//		if (East.name().equals(str))
//			return North;
//		if (SouthEast.name().equals(str))
//			return North;
//		if (South.name().equals(str))
//			return South;
//		if (SouthWest.name().equals(str))
//			return North;
//		if (West.name().equals(str))
//			return North;
//		if (NorthWest.name().equals(str))
//			return North;
//		return Center;
//	}
	
	public static String toString(Anchor ex) {
		
		switch (ex) {
		case Center:
			return "Center";
		case North:
			return "North";
		case South:
			return "South";
		case East:
			return "East";
		case West:
			return "West";
		case NorthEast:
			return "NortEast";
		case SouthEast:
			return "SouthEast";
		case SouthWest:
			return "SouthWest";
		case NorthWest:
			return "NorthWest";
		}
		return "";
	}
	
}
