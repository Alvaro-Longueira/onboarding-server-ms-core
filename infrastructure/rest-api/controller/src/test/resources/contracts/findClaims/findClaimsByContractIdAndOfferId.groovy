package contracts.findClaims

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description "Find Claims - Fetch Claims by 'contract_id and offer_id'"
    request {
        method GET()
        urlPath "/claims?contract_id=contract&offer_id=offer"
        headers {
            contentType("application/json")
        }
    }
    response {
        status 200
    }
}
