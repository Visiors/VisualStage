package com.visiors.visualstage.graph.view.node.impl;

import java.awt.Point;
import java.awt.Rectangle;

import com.visiors.visualstage.constants.PropertyConstants;
import com.visiors.visualstage.graph.view.node.Port;
import com.visiors.visualstage.graph.view.node.PortSet;
import com.visiors.visualstage.property.PropertyList;
import com.visiors.visualstage.property.impl.DefaultPropertyList;

public class DefaultPortSet implements PortSet {

	protected Port[] ports;
	protected PropertyList properties = new DefaultPropertyList(PropertyConstants.PORT_PROPERTY);
	private Positioning positioning;


	public DefaultPortSet() {

		this.ports = new DefaultPort[0];
		positioning = Positioning.FLOATING;
	}

	public DefaultPortSet(Port[] ports) {

		this.ports = new Port[ports.length];
		for (int i = 0; i < ports.length; i++) {
			this.ports[i] = ports[i].deepCopy();
		}
	}

	@Override
	public void createDefaultFourPortSet() {

		ports = new Port[4];
		// north port
		ports[0] = new DefaultPort(0, 0.5, 0.0, new int[] { 45, 135 });
		// south port
		ports[1] = new DefaultPort(1, 0.5, 1.0, new int[] { 225, 315 });
		// east port
		ports[2] = new DefaultPort(2, 1.0, 0.5, new int[] { 315, 45 });
		// west port
		ports[3] = new DefaultPort(3, 0.0, 0.5, new int[] { 135, 225 });
	}

	@Override
	public void createDefaultEightPortSet() {

		// TODO

	}

	@Override
	public Port[] getPorts() {

		return ports;
	}

	@Override
	public void updatePosition(Rectangle r) {

		for (Port port : ports) {
			port.updatePosition(r);
		}
	}

	@Override
	public PortSet deepCopy() {

		return new DefaultPortSet(getPorts());
	}

	@Override
	public Port getPortByID(int portID) {

		if (ports != null) {
			for (Port port : ports) {
				if (port.getID() == portID) {
					return port;
				}
			}
		}
		return null;
	}

	@Override
	public Port getPortAt(Point pt) {

		if (ports != null) {
			for (Port port : ports) {
				if (port.isHit(pt)) {
					return port;
				}
			}
		}
		return null;
	}

	@Override
	public int getPortNextTo(Point pt) {

		if (ports != null && ports.length > 0) {
			int p = ports[0].getID();
			int dist = Integer.MAX_VALUE;

			for (int i = 0, d; i < ports.length; i++) {
				Point pos = ports[i].getPosition();
				d = (int) Math.abs(Math.sqrt(Math.pow(pt.x - pos.x, 2) + Math.pow(pt.y - pos.y, 2)));
				if (d < dist) {
					dist = d;
					p = i;
				}
			}
			return ports[p].getID();
		}
		return -1;
	}

	@Override
	public int getPortByAngle(double angle) {

		if (ports != null) {
			for (Port port : ports) {
				if (port.acceptsDirection(angle)) {
					return port.getID();
				}
			}
		}

		return -1;
	}

	@Override
	public void setProperties(PropertyList properties) {

		if (properties != null) {
			ports = new DefaultPort[properties.size()];
			for (int i = 0; i < properties.size(); i++) {
				ports[i] = new DefaultPort();
				ports[i].setProperties((PropertyList) properties.get(i));
			}
		}
	}

	@Override
	public PropertyList getProperties() {

		if (properties == null) {
			if (ports != null && ports.length > 0) {
				properties = new DefaultPropertyList(PropertyConstants.PORTS_PROPERTY);
				for (Port port : ports) {
					properties.add(port.getProperties());
				}
			}
		}
		return properties;
	}

	@Override
	public Positioning getPositioning() {

		return positioning;
	}

	@Override
	public void setPositioning(Positioning positioning) {

		this.positioning = positioning;		
	}

}
