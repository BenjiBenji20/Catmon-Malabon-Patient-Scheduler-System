<h1 align="center">
  Catmon Malabon Patient Scheduler System
</h1>

<p align="center">
    <img src="https://github.com/user-attachments/assets/e23fa73b-a813-43a8-b628-23988d7a0856" width="1200"/>
</p>
<p>
  A Spring Boot application designed to manage patient appointments, doctor schedules, and medical records for Catmon Malabon Health Center.
</p>

---

<div align="center">
  <div>
    <img src="https://img.shields.io/badge/capstone-2025-red?style=for-the-badge&logo=github" />
  </div>
<br>
  <p align="center">
    <p><em>Built with the tools and technologies:</em></p>
    <img src="https://img.shields.io/badge/Java-38.5%25-orange?style=for-the-badge&logo=java&logoColor=white" />
    <img src="https://img.shields.io/badge/JavaScript-23.4%25-F7DF1E?style=for-the-badge&logo=javascript&logoColor=black" />
    <img src="https://img.shields.io/badge/HTML-21.8%25-E34F26?style=for-the-badge&logo=html5&logoColor=white" />
    <img src="https://img.shields.io/badge/CSS-16.3%25-1572B6?style=for-the-badge&logo=css3&logoColor=white" />
  </p>

  <p>

![XML](https://img.shields.io/badge/XML-007ACC?style=for-the-badge&logo=xml&logoColor=white)
![Twilio](https://img.shields.io/badge/Twilio-F22F46?style=for-the-badge&logo=twilio&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-4479A1?style=for-the-badge&logo=mysql&logoColor=white)
    
  </p>
<br>
  <p align="center">
    <p><em>Built Under Spring Framework Eco-System:</em></p>
    <img src="https://img.shields.io/badge/Spring%20Boot-6DB33F?style=for-the-badge&logo=springboot&logoColor=white" />
    <img src="https://img.shields.io/badge/Spring%20Data%20JPA-6DB33F?style=for-the-badge&logo=spring&logoColor=white" />
    <img src="https://img.shields.io/badge/Spring%20Security-6DB33F?style=for-the-badge&logo=springsecurity&logoColor=white" />
    <img src="https://img.shields.io/badge/Spring%20AI-00BC8C?style=for-the-badge&logo=spring&logoColor=white" />
  </p>
</div>

---

<div>
  <div>
    <h2>Table of Contents</h2>
    
- [Overview](#overview)
- [Features](#features)
- [Dependencies](#dependencies)
- [Project Setup](#project-setup)

  </div>

  <br>
  <div>
    <h3>Overview</h3><br>
    <p>
      Catmon-Malabon-Patient-Scheduler-System is a comprehensive healthcare management platform built on Spring Boot, designed to streamline 
      operations at health centers with secure, scalable backend capabilities. It integrates core functionalities such as data management, 
      real-time communication, and role-based security to support efficient healthcare workflows.
    </p>
    <br>
    <h3>Features</h3><br>
    
**🩺 Patient Management**
 - 👤 Register patients with personal details and assign them appointment schedules.

 - 🧮 Filter patients by gender, age range, or appointment status.

 - 📅 View patients scheduled for the current day for quick overview and preparation.

**🧑‍⚕️ Doctor Dashboard**
 - 📝 Assign available doctors to incoming appointments.

 - 📊 Monitor the number of patients handled per doctor.

 - 🔄 Update patient statuses (e.g., PENDING, COMPLETED) for clear tracking.

**🗓️ Appointment System**
 - ⏰ Check available slots in real-time to avoid scheduling conflicts.

 - ⚙️ Automatically assign doctors based on availability and workload.

 - 🚫 Prevent overbooking by limiting to a maximum of 50 patients per day.

**🔐 Security**
 - 🛡️ Implement JWT-based authentication to verify users securely.

 - 🧑‍💼 Enforce role-based access control (e.g., ADMIN, DOCTOR) to limit permissions appropriately.

**🔔 Real-Time Updates**
 - 🌐 WebSocket integration provides live notifications for new appointments and status updates.

 - 📲 Users receive immediate feedback on appointment changes and doctor assignments.
      
  </div>

---

<br>
<div>
  <h3>Dependencies</h3><br>
  
  [Download Dependencies](https://start.spring.io/) in Spring Initializr
  
 - org.springframework.boot:spring-boot-starter-data-jpa

 - org.springframework.boot:spring-boot-starter-security

 - org.springframework.boot:spring-boot-starter-validation

 - org.springframework.boot:spring-boot-starter-web

 - org.springframework.boot:spring-boot-starter-websocket

 - org.springframework.boot:spring-boot-devtools

 - com.mysql:mysql-connector-j

 - org.springframework.boot:spring-boot-starter-test

 - org.springframework.security:spring-security-test

 - org.springframework.ai:spring-ai-openai-spring-boot-starter

</div>

---

<br>
<div>
  <h3>Project Setup</h3>
  <br>
  
  Update **application.properties**
  
  ```bash
spring.application.name=Catmon Malabon Health Center System
server.port=8002

# DB and Driver configuration
spring.datasource.url=jdbc:mysql://localhost:3306/your_database
spring.datasource.username=your_username
spring.datasource.password=your_password
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# View sql queries
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.format-sql=true

# HikariCP configuration
spring.datasource.hikari.minimum-idle=5
spring.datasource.hikari.maximum-pool-size=10
spring.datasource.hikari.auto-commit=true
spring.datasource.hikari.isolation-level=TRANSACTION_READ_COMMITED

# Logging configuration
logging.level.org.hibernate.SQL=DEBUG
logging.level.org.hibernate.type.descriptor.sql.BasicBinder=TRACE
logging.level.com.zaxxer.hikari=DEBUG

# Twilio
twilio.acc.sid=your_twilio_sid
twilio.auth.key=your_twilio_key
twilio.my.phone.number=your_twilio_phone_number

# security config
jwt.secret.key=${SECRET_KEY}
frontend.origin=${FRONTEND_ORIGIN}
spring.security.user.name=username
spring.security.user.password=your_password
# security framework debug
logging.level.org.springframework.security=DEBUG

# DeepSeek API
#openrouter.api-key=your_openai_api_key
#openrouter.api.url=https:/your_https_address
#openrouter.model=deepseek-chat
```

</div>

<br><br>

**🏥 Streamlining Healthcare Appointments for Catmon Malabon!**
  
</div>



