package tw.pcbill.ksstarter

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class KsstarterApplication

fun main(args: Array<String>) {
	runApplication<KsstarterApplication>(*args)
}
