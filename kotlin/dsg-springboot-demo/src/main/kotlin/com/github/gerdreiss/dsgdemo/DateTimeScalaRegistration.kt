package com.github.gerdreiss.dsgdemo

import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsRuntimeWiring
import graphql.scalars.ExtendedScalars
import graphql.schema.idl.RuntimeWiring

/**
 * graphql-java provides optional scalars in the graphql-java-extended-scalars library.
 * We can wire a scalar from this library by adding the scalar to the RuntimeWiring.
 */
@DgsComponent
class DateTimeScalarRegistration {

    @DgsRuntimeWiring
    fun addScalar(builder: RuntimeWiring.Builder): RuntimeWiring.Builder {
        return builder
            .scalar(ExtendedScalars.Date)
            .scalar(ExtendedScalars.DateTime)
            .scalar(ExtendedScalars.UUID)
    }
}