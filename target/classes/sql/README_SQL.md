# SQL Deployment Guide for Order Grabbing System

## Database Setup Instructions

### Prerequisites
- MySQL Server 8.0+ installed and running
- MySQL command line client or a GUI tool like MySQL Workbench
- Database user with CREATE DATABASE and CREATE TABLE privileges

### Deployment Steps

#### Option 1: Using MySQL Command Line

1. **Connect to MySQL Server**
   ```bash
   mysql -u root -p
   ```
   Enter your password when prompted.

2. **Run the initialization script**
   ```bash
   source /path/to/init_database.sql;
   ```
   Or from the command line directly:
   ```bash
   mysql -u root -p < src/main/resources/sql/init_database.sql
   ```

3. **Verify the deployment**
   ```sql
   USE order_grabbing;
   SHOW TABLES;
   SELECT * FROM grab_orders;
   ```

#### Option 2: Using MySQL Workbench

1. Open MySQL Workbench and connect to your server
2. Go to File > Open SQL Script
3. Select `init_database.sql`
4. Click the "Execute" button (⚡)
5. Verify the tables and data are created

### Database Configuration

The application uses the following database configuration (in `application.properties`):
- Database: `order_grabbing`
- Username: `root`
- Password: `123456`
- URL: `jdbc:mysql://localhost:3306/order_grabbing`

**Note:** For production deployment, please change the default credentials and consider:
1. Creating a dedicated database user with limited permissions
2. Using strong passwords
3. Enabling SSL connections
4. Configuring proper firewall rules

### Scripts Description

| File | Description |
|------|-------------|
| `init_database.sql` | Main initialization script - creates database, tables, and sample data |
| `cleanup_database.sql` | Cleanup script - drops all tables (use with caution!) |

### Table Structures

#### grab_orders Table
| Column | Type | Description |
|--------|------|-------------|
| grab_id | BIGINT | Primary key, auto-incrementing |
| start_time | DATETIME | Grab activity start time |
| end_time | DATETIME | Grab activity end time |
| product_name | VARCHAR(100) | Name of the product being grabbed |
| stock | INT | Available stock quantity |

#### orders Table
| Column | Type | Description |
|--------|------|-------------|
| order_id | BIGINT | Primary key, auto-incrementing |
| phone_number | VARCHAR(20) | User's phone number |
| grab_id | BIGINT | Foreign key to grab_orders |
| order_status | VARCHAR(20) | Order status (SUCCESS/FAILED) |
| create_time | DATETIME | Order creation timestamp |

### Troubleshooting

1. **Connection Issues**
   - Verify MySQL server is running: `systemctl status mysql` (Linux) or check Services (Windows)
   - Ensure port 3306 is open and not blocked by firewall
   - Check connection string in `application.properties`

2. **Permission Issues**
   - Ensure the database user has CREATE and INSERT privileges
   - Verify password is correct in `application.properties`

3. **Character Encoding Issues**
   - The scripts use `utf8mb4` character set for full Unicode support
   - Ensure your MySQL server is configured with proper character set settings

### Production Recommendations

1. **Backup** - Always backup existing data before deployment
2. **Test** - Test the scripts in a staging environment first
3. **Monitoring** - Set up database monitoring and alerts
4. **Indexing** - Additional indexes may be needed based on query patterns
5. **Partitioning** - Consider table partitioning for large datasets
6. **Replication** - Set up database replication for high availability
