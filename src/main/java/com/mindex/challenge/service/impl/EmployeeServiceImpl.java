package com.mindex.challenge.service.impl;

import com.mindex.challenge.dao.EmployeeRepository;
import com.mindex.challenge.data.Employee;
import com.mindex.challenge.data.ReportingStructure;
import com.mindex.challenge.service.EmployeeService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    private static final Logger LOG = LoggerFactory.getLogger(EmployeeServiceImpl.class);

    @Autowired
    private EmployeeRepository employeeRepository;
    
    @Override
    public Employee create(Employee employee) {
        LOG.debug("Creating employee [{}]", employee);

        employee.setEmployeeId(UUID.randomUUID().toString());
        employeeRepository.insert(employee);

        return employee;
    }

    @Override
    public Employee read(String id) {
        LOG.debug("Reading employee with id [{}]", id);

        Employee employee = findEmployee(id);

        return employee;
    }

    @Override
    public Employee update(Employee employee) {
        LOG.debug("Updating employee [{}]", employee);
        
        return employeeRepository.save(employee);
    }
    
    @Override
    public ReportingStructure report(String id) {
    	LOG.debug("Creating reporting structure for employee with id [{}]", id);
    	
    	Employee employee = findEmployee(id);
    	int numberOfReports = genNumberOfReports(employee);
    	
    	return new ReportingStructure(employee, numberOfReports);
    }
    
    //Method to find an employee in the database by their id - throws runtime error if not found
    @Override
    public Employee findEmployee(String id) {
    	Employee employee = employeeRepository.findByEmployeeId(id);
        if (employee == null) {
            throw new RuntimeException("Invalid employeeId: " + id);
        }
        return employee;
    }
    
    //Method to determine the number of direct reports of an employee on the fly
    public int genNumberOfReports(Employee employee) {
		List<Employee> currentReports = employee.getDirectReports();
		//If current reports was never initialized, number of direct reports is 0
		if (currentReports == null) {
			return 0;
		}
		int reportSum = currentReports.size();
		for (Employee e : currentReports) {
			try {
				reportSum += genNumberOfReports(findEmployee(e.getEmployeeId()));
			}
			catch(RuntimeException ex) {
				/* Do nothing - if employee is not in database,
				 * cannot cycle through their direct reports, so
				 * treat them as a leaf node
				 */
			}
		}
		return reportSum;
	}
	
}
