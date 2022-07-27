package contracts.findClaims

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    ignored()
    description "Find Claims - Fetch Claims with unsupported sort parameter"
    request {
        method GET()
        urlPath "/claims?invalid=invalid"
        headers {
            contentType("application/json")
        }
    }
    response {
        status 400
    }
}
