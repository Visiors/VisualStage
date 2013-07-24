package com.visiors.visualstage.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.visiors.visualstage.export.XMLService;
import com.visiors.visualstage.property.Property;
import com.visiors.visualstage.property.PropertyList;
import com.visiors.visualstage.property.PropertyUnit;
import com.visiors.visualstage.property.impl.DefaultPropertyList;
import com.visiors.visualstage.property.impl.DefaultPropertyUnit;
import com.visiors.visualstage.property.impl.PropertyType;

public class PropertyUtil {

	private static final String SEPARATOR = ":";

	public static String propertyList2XML(PropertyList properties, boolean insertHeader) {

		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			XMLService xmlService = new XMLService();
			xmlService.propertyList2XML(bos, properties, insertHeader);
			bos.flush();
			return bos.toString();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static PropertyList XML2PropertyList(String strXML) {

		try {
			ByteArrayInputStream bis = new ByteArrayInputStream(strXML.getBytes());
			XMLService xmlService = new XMLService();
			return xmlService.XML2PropertyList(bis);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		}
		return null;
	}
	public static PropertyUnit findPropertyUnit(PropertyList pl, String fullPath) {
		if (pl == null) {
			return null;
		}

		String name = extractName(fullPath);
		String[] path = extractPath(fullPath);

		PropertyList root = pl;
		PropertyList next = root;

		for (int i = 0; i < path.length; i++) {
			if (!next.getName().equalsIgnoreCase(path[i])) {

				Property li = next.get(path[i]);
				if (li != null) {
					if (li instanceof PropertyUnit) {
						throw new IllegalArgumentException(path[i]
								+ " already exists as a PropertyUnit "
								+ "while a PropertyList is expected!");
					}
					next = (PropertyList) next.get(path[i]);
					continue;
				}

				// append list
				DefaultPropertyList newList = new DefaultPropertyList(path[i]);
				next.add(newList);
				next = newList;
				// add unit or list
			}
		}


		return (PropertyUnit)  next.get(name);
	}

	public static PropertyList findPropertyList(PropertyList pl, String fullPath) {
		if (pl == null) {
			return null;
		}

		String name = extractName(fullPath);
		String[] path = extractPath(fullPath);

		PropertyList root = pl;
		PropertyList next = root;

		for (int i = 0; i < path.length; i++) {
			if (!next.getName().equalsIgnoreCase(path[i])) {

				Property li = next.get(path[i]);
				if (li != null) {
					if (li instanceof PropertyUnit) {
						throw new IllegalArgumentException(path[i]
								+ " already exists as a PropertyUnit "
								+ "while a PropertyList is expected!");
					}
					next = (PropertyList) next.get(path[i]);
					continue;
				}

				// append list
				DefaultPropertyList newList = new DefaultPropertyList(path[i]);
				next.add(newList);
				next = newList;
				// add unit or list
			}
		}


		return (PropertyList)  next.get(name);
	}

	public static void setPropertyRange(PropertyList pl, String name, String rangeExpression) {
		PropertyUnit p = findPropertyUnit(pl, name);
		if (p == null) {
			new Throwable("Could not find the property unit [" + name+"] in the property list [" + pl.getName()+"]");
		}	
		p.setValueRangeExpression(rangeExpression);

	}




	public static PropertyList getPropertyList(PropertyList parent, String name) {
		return findPropertyList(parent, name);
	}


	public static Object getProperty(PropertyList pl, String name) {
		PropertyUnit p = findPropertyUnit(pl, name);
		if (p != null) {
			return p.getValue();
		}
		return null;
	}

	public static int getProperty(PropertyList pl, String name, int defaultValue) {
		PropertyUnit p = findPropertyUnit(pl, name);
		if (p != null) {
			try {
				return ConvertUtil.object2int(p.getValue());
			} catch (Exception e) {
				/* return default */
			}
		}
		//		System.err.println("Warning: used default value since " +name+ " could not be found in " + pl);
		return defaultValue;
	}

	public static double getProperty(PropertyList pl, String name, double defaultValue) {
		PropertyUnit p = findPropertyUnit(pl, name);
		if (p != null) {
			try {
				return ConvertUtil.object2double(p.getValue());
			} catch (Exception e) {
				/* return default */
			}
		}
		return defaultValue;
	}

	public static long getProperty(PropertyList pl, String name, long defaultValue) {
		PropertyUnit p = findPropertyUnit(pl, name);
		if (p != null) {
			try {
				return ConvertUtil.object2long(p.getValue());
			} catch (Exception e) {
				/* return default */
			}
		}
		return defaultValue;
	}

	public static boolean getProperty(PropertyList pl, String name, boolean defaultValue) {
		PropertyUnit p = findPropertyUnit(pl, name);
		if (p != null) {
			try {
				return ConvertUtil.object2boolean(p.getValue());
			} catch (Exception e) {
				/* return default */
			}
		}
		return defaultValue;
	}

	public static String getProperty(PropertyList pl, String name, String defaultValue) {
		PropertyUnit p = findPropertyUnit(pl, name);
		if (p != null) {
			try {
				return ConvertUtil.object2string(p.getValue());
			} catch (Exception e) {
				/* return default */
			}
		}
		return defaultValue;
	}


	public static PropertyList setProperty(PropertyList pl, String fullPath, int value, String valueRange) {
		return setProperty(pl, fullPath, new Integer(value), PropertyType.INTEGER);
	}
	public static PropertyList setProperty(PropertyList pl, String fullPath, int value) {
		return setProperty(pl, fullPath, new Integer(value), PropertyType.INTEGER);
	}

	public static PropertyList setProperty(PropertyList pl, String fullPath, long value) {
		return setProperty(pl, fullPath, new Long(value), PropertyType.LONG);
	}

	public static PropertyList setProperty(PropertyList pl, String fullPath, String value) {
		return setProperty(pl, fullPath, (Object)value, PropertyType.STRING);
	}

	public static PropertyList setProperty(PropertyList pl, String fullPath, String value, PropertyType type) {
		return setProperty(pl, fullPath, (Object)value, type);
	}

	public static PropertyList setProperty(PropertyList pl, String fullPath, boolean value) {
		return setProperty(pl, fullPath, new Boolean(value), PropertyType.BOOLEAN);
	}

	public static PropertyList setProperty(PropertyList pl, String fullPath, double value) {
		return setProperty(pl, fullPath, new Double(value), PropertyType.DOUBLE);
	}

	public static PropertyList setProperty(PropertyList pl, String fullPath, Object value) {
		return setProperty(pl, fullPath, value, PropertyType.OBJECT);
	}

	public static PropertyList setProperty(PropertyList root, String fullPath, Object value, PropertyType type) {

		String name = extractName(fullPath);
		String[] path = extractPath(fullPath);
		PropertyList next = root;

		// create the property chain according to path if not exists already
		for (int i = 0; i < path.length; i++) {
			if (root == null) {
				root = new DefaultPropertyList(path[0]);
				next = root;
			} else if (!next.getName().equalsIgnoreCase(path[i])) {
				Property li = next.get(path[i]);
				if (li != null) {
					if (isPropertyUnit(li)) {
						throw new IllegalArgumentException(path[i]
								+ " already exists as a PropertyUnit "
								+ "while a PropertyList is expected!");
					}

					next = (PropertyList) next.get(path[i]);
					continue;
				}

				// append list
				DefaultPropertyList newList = new DefaultPropertyList(path[i]);
				next.add(newList);
				next = newList;
				// add unit or list
			}
		}

		insertOrUpdateProperty(next, name, value, type);

		return root;

	}

	public static String[] extractPath(String fullPath) {
		if (fullPath.indexOf(SEPARATOR) == -1) {
			return new String[0];
		}
		String[] parts = fullPath.split(SEPARATOR);
		String[] path = new String[parts.length - 1];
		System.arraycopy(parts, 0, path, 0, path.length);
		return path;
	}

	public static String extractName(String fullPath) {
		int n = fullPath.lastIndexOf(SEPARATOR);
		if (n == -1) {
			return fullPath;
		}
		return fullPath.substring(n + 1);
	}


	public static PropertyList insertOrUpdateProperty(PropertyList pl, String name, Object value, PropertyType type) {

		if (pl == null) {
			throw new IllegalArgumentException("PropertyList must not be null!");
		}

		Property p = pl.get(name);
		if (p != null) {
			if (isPropertyUnit(pl)) {
				// error
				throw new IllegalArgumentException(name + " already exists as a PropertyUnit "
						+ "while a PropertyList is expected!");
			} else { // update value
				((PropertyUnit) p).setValue(value);
			}
		} else {
			if(value instanceof PropertyList) {
				DefaultPropertyList newList = new DefaultPropertyList(name);
				pl.add(newList);
				newList.add((PropertyList) value);
			} else {
				pl.add(new DefaultPropertyUnit(name, value, type));
			}
		}
		return pl;
	}

	private static boolean isPropertyList(Property p) {
		return p instanceof PropertyList;
	}

	private static boolean isPropertyUnit(Property p) {
		return p instanceof PropertyUnit;
	}

	public static String toString(List<PropertyList> path, PropertyUnit property ) {

		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < path.size(); i++) {
			sb.append(path.get(i).getName());
			sb.append(':');
		}
		sb.append(property.getName());
		return sb.toString();
	}

}
