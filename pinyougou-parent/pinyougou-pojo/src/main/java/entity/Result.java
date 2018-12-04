package entity;

public class Result {
	private String message;
	private Boolean success;
	public Result() {
		super();
		// TODO Auto-generated constructor stub
	}
	public Result(Boolean success,String message) {
		super();
		this.message = message;
		this.success = success;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public Boolean getSuccess() {
		return success;
	}
	public void setSuccess(Boolean success) {
		this.success = success;
	}
	

}
