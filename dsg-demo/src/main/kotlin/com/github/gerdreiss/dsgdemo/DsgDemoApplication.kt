package com.github.gerdreiss.dsgdemo

import graphql.scalars.ExtendedScalars
import graphql.schema.idl.RuntimeWiring
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class DsgDemoApplication

fun main(args: Array<String>) {
    runApplication<DsgDemoApplication>(*args)
}
