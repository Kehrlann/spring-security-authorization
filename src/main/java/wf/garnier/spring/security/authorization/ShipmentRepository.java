package wf.garnier.spring.security.authorization;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Component;

@Component
class ShipmentRepository {

	private final List<Shipment> shipments = List.of(
			new Shipment(UUID.randomUUID(), "10 Downing Street, London", Shipment.Status.PREPARING, List.of()),
			new Shipment(UUID.randomUUID(), "Bag End, Hobbiton", Shipment.Status.DELIVERING,
					List.of("Received package", "Shipped to processing center", "Left processing center")),
			new Shipment(UUID.randomUUID(), "221B Baker Street, London", Shipment.Status.UNKNOWN,
					List.of("Picked up at center by a M. Moriarty")),
			new Shipment(UUID.randomUUID(), "Palau de Congressos, Av. de la Reina Maria Cristina",
					Shipment.Status.DELIVERED, List.of("What a great conference ❤️")));

	public List<Shipment> findAll() {
		return shipments;
	}

}
