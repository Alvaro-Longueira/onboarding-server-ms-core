package contracts.createClaim

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description "Create Claim - Wrong Format"
    request {
        method POST()
        urlPath "/claims"
        headers {
            contentType("application/json")
        }
        body('''
            {
              "claim_id": "CLM_11111111",
              "type": "INVALID",
              "event_date": "2021-02-24T12:43:03.340Z",
              "notification_date": "2021-02-24T12:43:03.340Z",
              "contract_id": "CTC_11111111",
              "offer_id": "OFR_11111111",
              "account_id": "ACT_11111111",
              "product_id": "PDT_11111111",
              "without_coverage": true
            }
        ''')
    }
    response {
        status 422
    }
}
