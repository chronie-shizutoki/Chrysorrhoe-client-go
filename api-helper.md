API Endpoints
Health Check
GET /api/health
Description: Check server health status
Response: {"status":"UP","timestamp":"<current-time>","environment":"<env>"}
Wallet Operations
POST /api/wallets

Description: Create a new wallet
Request Body: {"username":"<username>"} (username must be a unique string)
Response: {"success":true,"wallet":{"id":"<wallet-id>","username":"<username>","balance":0,"createdAt":"<timestamp>"}}
Errors: 400 (Username already exists/invalid), 500 (Server error)
GET /api/wallets/:walletId

Description: Get wallet information by wallet ID
Path Parameters: walletId (Wallet unique identifier)
Response: {"success":true,"wallet":{"id":"<wallet-id>","username":"<username>","balance":<balance>,"createdAt":"<timestamp>","updatedAt":"<timestamp>"}}
Errors: 404 (Wallet not found), 500 (Server error)
GET /api/wallets/username/:username

Description: Get wallet information by username
Path Parameters: username (Wallet holder username)
Response: {"success":true,"wallet":{"id":"<wallet-id>","username":"<username>","balance":<balance>,"createdAt":"<timestamp>","updatedAt":"<timestamp>"}}
Errors: 404 (Wallet not found), 500 (Server error)
PUT /api/wallets/:walletId/balance

Description: Update wallet balance
Path Parameters: walletId (Wallet unique identifier)
Request Body: {"amount":<new-balance>} (must be a non-negative number)
Response: {"success":true,"wallet":{"id":"<wallet-id>","username":"<username>","balance":<new-balance>,"createdAt":"<timestamp>","updatedAt":"<timestamp>"}}
Errors: 400 (Invalid amount), 404 (Wallet not found), 500 (Server error)
GET /api/wallets/:walletId/transactions

Description: Get wallet transaction history
Path Parameters: walletId (Wallet unique identifier)
Query Parameters: page=1, limit=10 (Pagination parameters, limit range 1-100)
Response: {"success":true,"transactions":[],"pagination":{"currentPage":1,"totalPages":1,"totalTransactions":10,"limit":10,"hasNextPage":false,"hasPreviousPage":false}}
Errors: 400 (Invalid pagination parameters), 404 (Wallet not found), 500 (Server error)
GET /api/wallets/:walletId/transactions/detailed

Description: Get wallet detailed transaction history (includes recipient username)
Path Parameters: walletId (Wallet unique identifier)
Query Parameters: page=1, limit=10 (Pagination parameters, limit range 1-100)
Response: {"success":true,"transactions":[],"pagination":{"currentPage":1,"totalPages":1,"totalTransactions":10,"limit":10,"hasNextPage":false,"hasPreviousPage":false}}
Errors: 400 (Invalid pagination parameters), 404 (Wallet not found), 500 (Server error)
Transaction Operations
GET /api/transactions/:transactionId

Description: Get detailed transaction information by transaction ID
Path Parameters: transactionId (Transaction unique identifier)
Response: {"success":true,"transaction":{"id":"<transaction-id>","fromWalletId":"<wallet-id>","toWalletId":"<wallet-id>","amount":<amount>,"transactionType":"<type>","description":"<description>","createdAt":"<timestamp>"}}
Errors: 404 (Transaction not found), 500 (Server error)
GET /api/transactions

Description: Get all transaction records (supports pagination and type filtering)
Query Parameters: page=1, limit=10, type=transfer (Optional, filter by transaction type)
Response: {"success":true,"transactions":[],"pagination":{"currentPage":1,"totalPages":1,"totalTransactions":10,"limit":10,"hasNextPage":false,"hasPreviousPage":false}}
Errors: 400 (Invalid pagination parameters), 500 (Server error)
Transfer Operations
POST /api/transfers

Description: Execute a transfer using wallet IDs
Request Body: {"fromWalletId":"<wallet-id>","toWalletId":"<wallet-id>","amount":<amount>,"description":"<description>"} (amount must be greater than 0, description is optional)
Response: {"success":true,"transaction":{...},"fromWallet":{...},"toWallet":{...}}
Errors: 400 (Insufficient balance/Transferring to self/Invalid parameters), 404 (Wallet not found), 500 (Server error)
POST /api/transfers/by-username

Description: Execute a transfer using wallet usernames
Request Body: {"fromUsername":"<username>","toUsername":"<username>","amount":<amount>,"description":"<description>"} (amount must be greater than 0, description is optional)
Response: {"success":true,"transaction":{...},"fromWallet":{...},"toWallet":{...}}
Errors: 400 (Insufficient balance/Transferring to self/Invalid parameters), 404 (Wallet not found), 500 (Server error)
Exchange Rate Operations
GET /api/exchange-rates/latest

Description: Get the latest exchange rate
Response: {"success":true,"rate":{"id":"<rate-id>","rate":<rate>,"createdAt":"<timestamp>","updatedAt":"<timestamp>"}}
Errors: 500 (Server error)
GET /api/exchange-rates

Description: Get the list of exchange rate records
Query Parameters: page=1, limit=10 (Pagination parameters, limit range 1-100)
Response: {"success":true,"rates":[],"pagination":{"currentPage":1,"totalPages":1,"totalRecords":10,"limit":10,"hasNextPage":false,"hasPreviousPage":false}}
Errors: 500 (Server error)
POST /api/exchange-rates/refresh

Description: Manually refresh the exchange rate
Response: {"success":true,"message":"Exchange rate refreshed successfully","rate":{...}}
Errors: 500 (Server error)
DELETE /api/exchange-rates/cleanup

Description: Clean up old exchange rate records
Request Body: {"beforeDate":"<date>"} (must be a valid date format)
Response: {"success":true,"message":"Successfully deleted <count> exchange rate records"}
Errors: 400 (Invalid date format), 500 (Server error)
Interest Operations
POST /api/interests/process

Description: Manually trigger interest calculation (admin only)
Response: {"success":true,"message":"Interest calculation executed successfully","data":{"processedCount":<count>,"totalInterest":<amount>}}
Errors: 500 (Server error)
GET /api/interests/status

Description: Get the status of the interest scheduler
Response: {"success":true,"data":{"isRunning":true,"nextExecutionTime":"<timestamp>","timezone":"UTC"}}
Errors: 500 (Server error)
Third-Party Payment Operations
POST /api/third-party/payments

Description: Execute a third-party payment (fees apply: 30% of the transaction amount)
Request Body: {"walletId":"<wallet-id>","username":"<username>","amount":<amount>,"thirdPartyId":"<third-party-id>","thirdPartyName":"<third-party-name>","description":"<description>"} (Either wallet ID or username must be provided. Amount must be greater than 0 and have at most 2 decimal places.)
Response: {"success":true,"transaction":{...},"wallet":{...},"thirdPartyInfo":{...}}
Errors: 400 (Insufficient balance/Invalid parameters), 404 (Wallet not found), 500 (Server error)
POST /api/third-party/receipts

Description: Process third-party receipts
Request Body: {"walletId":"<wallet-id>","username":"<username>","amount":<amount>,"thirdPartyId":"<third-party-id>","thirdPartyName":"<third-party-name>","description":"<description>"} (Either wallet ID or username must be provided. Amount must be greater than 0 and have at most 2 decimal places.)
Response: {"success":true,"transaction":{...},"wallet":{...},"thirdPartyInfo":{...}}
Errors: 400 (Invalid parameters), 404 (Wallet not found), 500 (Server error)
GET /api/third-party/transactions

Description: Get third-party transaction records
Query Parameters: walletId=<wallet-id>, username=<username>, page=1, limit=10 (Wallet ID or username is optional. Pagination parameters.)
Response: {"success":true,"transactions":[],"pagination":{...}}
Errors: 400 (Invalid pagination parameters), 404 (Wallet not found), 500 (Server error)