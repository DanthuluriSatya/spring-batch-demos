package com.demo.springbatch.model;

public class StudentJson {
	
		private Long id;
		
		private String firstName;

		private String lasstName;
		
		private String email;

		public Long getId() {
			return id;
		}

		public void setId(Long id) {
			this.id = id;
		}

		public String getFirstName() {
			return firstName;
		}

		public void setFirstName(String firstName) {
			this.firstName = firstName;
		}

		public String getLasstName() {
			return lasstName;
		}

		public void setLasstName(String lasstName) {
			this.lasstName = lasstName;
		}

		public String getEmail() {
			return email;
		}

		public void setEmail(String email) {
			this.email = email;
		}

		@Override
		public String toString() {
			return "StudentCsv [id=" + id + ", firstName=" + firstName + ", lasstName=" + lasstName + ", email=" + email
					+ "]";
		}
		

	}


}
