import com.eps.plugin.chatbot.annotation.Generated;

/** 
 * Generated code by CARET Assistant
 */
@Generated(agent = "GEMINI-2.5-PRO", task = "CREATE_CLASS", id = "1750615117714", timestamp = "2025-06-22 19:58:37")
public class Airline {
	private String name;
	private String address;

	/** 
	* Default constructor for the Airline class.
	*/
	public Airline() {
	}

	/** 
	* Constructs an Airline object with a specified name and address.
	* @param name    The name of the airline.
	* @param address The address of the airline's headquarters.
	*/
	public Airline(String name, String address) {
		this.name = name;
		this.address = address;
	}

	/** 
	* Gets the name of the airline.
	* @return The name of the airline.
	*/
	public String getName() {
		return name;
	}

	/** 
	* Sets the name of the airline.
	* @param name The new name for the airline.
	*/
	public void setName(String name) {
		this.name = name;
	}

	/** 
	* Gets the address of the airline.
	* @return The address of the airline.
	*/
	public String getAddress() {
		return address;
	}

	/** 
	* Sets the address of the airline.
	* @param address The new address for the airline.
	*/
	public void setAddress(String address) {
		this.address = address;
	}

	@Override
	public String toString() {
		return "Airline{" + "name='" + name + '\'' + ", address='" + address + '\'' + '}';
	}
}
