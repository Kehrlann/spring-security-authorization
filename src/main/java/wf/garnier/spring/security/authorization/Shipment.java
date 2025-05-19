package wf.garnier.spring.security.authorization;

import java.util.List;

class Shipment {

	private final int id;

	private final String address;

	private final Status status;

	private final List<String> events;

	public Shipment(int id, String address, Status status, List<String> events) {
		this.id = id;
		this.address = address;
		this.status = status;
		this.events = events;
	}

	public int getId() {
		return id;
	}

	public String getAddress() {
		return address;
	}

	public Status getStatus() {
		return status;
	}

	public List<String> getEvents() {
		return events;
	}

	enum Status {

		PREPARING, DELIVERING, DELIVERED, UNKNOWN

	}

}
