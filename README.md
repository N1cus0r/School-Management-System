# School-Management-System

The School Management System is a comprehensive web application designed to streamline the administrative and educational processes within a school. It provides distinct interfaces for administrators, teachers, and students, each tailored to their specific roles.

## Key Features

- **User Roles:**
  - **Admins:** Manage system settings, user accounts, and overall system functionality.
  - **Teachers:** Create and manage courses, homeworks, grades, comments and attendances, assign and remove students.
  - **Students:** Access course information, submit view homeworks, grades, comments and attendances

![Design fără titlu(2)](https://github.com/N1cus0r/School-Management-System/assets/110561950/f0a6cb3f-3779-4e53-a0b9-36aabf5c2f75)

- **Data Management:**
  - CRUD opperations for all the entities
  - Assign or remove students from courses.
  - Provide users with profile images

## Technologies Used

- **Backend:** Spring Boot, Java Persistence API (JPA), Hibernate, AWS S3.
- **Frontend:** React JS + Typescript.
- **Database:** PortgreSQL.
- **Authentication:** Spring Security.
- **Build Tool:** Maven.

## Getting Started
- Clone this repository using

```bash
git clone https://github.com/N1cus0r/School-Management-System.git
```

- Navigate to project directory

```bash
cd School-Management-System
```

- Start the containers and you are good to go

```bash
docker-compose up
```

The application will be available on **http://localhost:3000**
