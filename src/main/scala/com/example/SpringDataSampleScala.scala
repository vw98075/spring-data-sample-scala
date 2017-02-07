package com.example

import java.math.BigDecimal
import java.time.LocalDate
import java.util.Arrays
import javax.persistence._
import javax.validation.constraints.{DecimalMin, Digits, Size}

import com.example.Currency.Currency
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import org.springframework.data.rest.core.annotation.RepositoryRestResource
import org.springframework.format.annotation.DateTimeFormat

import scala.beans.BeanProperty

/**
  * Created by Vernon on 1/30/2017.
  */

@SpringBootApplication
class SpringDataSampleScalaApplication {

  @Bean private[example] def initData(bookRepository: BookRepository, authorRepository: AuthorRepository) = AnyRef -> {
    bookRepository.save(Book("Spring Microservices", "Learn how to efficiently build and implement microservices in Spring,\n" +
      "and how to use Docker and Mesos to push the boundaries. Examine a number of real-world use cases and hands-on code examples.\n" +
      "Distribute your microservices in a completely new way",
      LocalDate.of(2016, 6, 28), new Money(new BigDecimal(45.83))))
    bookRepository.save(new Book("Pro Spring Boot", "A no-nonsense guide containing case studies and best practise for Spring Boot",
      LocalDate.of(2016, 5, 21), new Money(new BigDecimal(42.74))))
  }
}

object SpringDataSampleScalaApplication{

  def main(args: Array[String]) {
    SpringApplication.run(classOf[SpringDataSampleScalaApplication], args: _*)
  }
}

@Entity
case class Book private[example](@BeanProperty @Size(min=1, max = 255) title: String,
                                 @BeanProperty @Size(min=1, max = 255) description: String,
                                 @BeanProperty publishedDate: LocalDate,
                                 @BeanProperty @Embedded price: Money){

  @BeanProperty
  @Id
  @GeneratedValue
  val id: java.lang.Long = 0L

  def this() {
    this(null, null, null, null)
  }
}

object Currency {
  type Currency = String
  val CAD = "CAD"
  val EUR = "EUR"
  val USD = "USD"
}

@Embeddable
case class Money private[example](@BeanProperty currency: Currency,
                                  @BeanProperty
                                    @DecimalMin(value = "0", inclusive = false)
                                    @Digits(integer = 1000000000, fraction = 2) amount: BigDecimal) {
  def this(amount: BigDecimal) {
    this(Currency.USD, amount)
  }

  def this() = this(null, null)
}


@RepositoryRestResource
trait BookRepository extends CrudRepository[Book, java.lang.Long] {
  def findByTitle(@Param("title") title: String): java.util.List[Book]

  def findByTitleContains(@Param("keyword") keyword: String): java.util.List[Book]

  def findByPublishedDateAfter(@Param("publishedDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) publishedDate: LocalDate): java.util.List[Book]

  def findByTitleContainsAndPublishedDateAfter(@Param("keyword") keyword: String,
                                               @Param("publishedDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) publishedDate: LocalDate): java.util.List[Book]

  def findByTitleContainsAndPriceCurrencyAndPriceAmountBetween(@Param("keyword") keyword: String,
                                                               @Param("currency") currency: Currency,
                                                               @Param("low") low: BigDecimal,
                                                               @Param("high") high: BigDecimal): java.util.List[Book]
}

@Entity
case class Author private[example](@BeanProperty @Size(min=1, max = 255) firstName: String,
                                   @BeanProperty @Size(min=1, max = 255) lastName: String) {
  @BeanProperty
  @Id
  @GeneratedValue
  val id = 0L

  def this() = this(null, null)
}

@RepositoryRestResource
trait AuthorRepository extends CrudRepository[Author, java.lang.Long] {

  def findByLastName(@Param("lastName") lastName: String): java.util.List[Author]
}