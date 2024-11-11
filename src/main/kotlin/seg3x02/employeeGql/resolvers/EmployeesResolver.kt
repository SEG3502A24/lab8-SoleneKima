package seg3x02.employeeGql.resolvers

import org.springframework.data.mongodb.core.MongoOperations
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.MutationMapping
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.graphql.data.method.annotation.SchemaMapping
import org.springframework.stereotype.Controller
import seg3x02.employeeGql.entity.Employee
import seg3x02.employeeGql.repository.EmployeesRepository
import seg3x02.employeeGql.resolvers.types.CreateEmployeeInput
import java.util.*

@Controller
class EmployeesResolver(private val employeesRepository: EmployeesRepository,
private val mongoOperations: MongoOperations) {
    @QueryMapping
    fun employees(): List<Employee> {
        return employeesRepository.findAll()
    }


    @QueryMapping
    fun employeeById(@Argument employeeId: String): Employee? {
        val employee = employeesRepository.findById(employeeId)
        return employee.orElse(null)
    }

    @QueryMapping
    fun employeeByName(@Argument employeeName: Name): Employee? {
        val query = Query()
        query.addCriteria(Criteria.where("employeeName").`is`(employeeName))
        val result = mongoOperations.find(query, Employee::class.java)
        return result.firstOrNull()
    }

    @MutationMapping
    fun newEmployee(@Argument("createEmployeeInput") input: CreateEmployeeInput) : Employee {
        if (input.name != null &&
                input.dateOfBirth != null &&
                input.city != null && 
                input.salary != null && 
                input.gender != null && input.email != null) {
            val employee = Employee(input.name, input.dateOfBirth, input.city, input.salary, input.gender, input.email)
            employee.employeeId = UUID.randomUUID().toString()
            employeesRepository.save(employee)
            return employee
        } else {
            throw Exception("Invalid input")
        }
    }

    @MutationMapping
    fun deleteEmployee(@Argument("employeeId") id: String) : Boolean {
        employeesRepository.deleteById(id)
        return true
    }

    @MutationMapping
    fun updateEmployee(@Argument employeeId: String, @Argument("createEmployeeInput") input: CreateEmployeeInput) : Employee {
        val employee = employeesRepository.findById(employeeId)
        employee.ifPresent {
            if (input.employeeName != null) {
                it.employeeName = input.employeeName
            }
            if (input.dateOfBirth != null) {
                it.dateOfBirth = input.dateOfBirth
            }
            if (input.city != null) {
                it.city = input.city
            }
            if (input.salary != null) {
                it.salary = input.salary
            }
            if (input.gender != null) {
                it.gender = input.gender
            }
            if (input.email != null) {
                it.email = input.email
            }
            employeesRepository.save(it)
        }
        return employee.get()
    }
}
