# 创建Android项目所需的目录结构

# UI相关目录
mkdir -p android\app\src\main\java\com\chronie\chrysorrhoego\ui\auth
mkdir -p android\app\src\main\java\com\chronie\chrysorrhoego\ui\wallet
mkdir -p android\app\src\main\java\com\chronie\chrysorrhoego\ui\transaction
mkdir -p android\app\src\main\java\com\chronie\chrysorrhoego\ui\cdk
mkdir -p android\app\src\main\java\com\chronie\chrysorrhoego\ui\adapter
mkdir -p android\app\src\main\java\com\chronie\chrysorrhoego\ui\component

# 数据层目录
mkdir -p android\app\src\main\java\com\chronie\chrysorrhoego\data\remote\api
mkdir -p android\app\src\main\java\com\chronie\chrysorrhoego\data\remote\dto
mkdir -p android\app\src\main\java\com\chronie\chrysorrhoego\data\remote\interceptor
mkdir -p android\app\src\main\java\com\chronie\chrysorrhoego\data\local\database
mkdir -p android\app\src\main\java\com\chronie\chrysorrhoego\data\local\models
mkdir -p android\app\src\main\java\com\chronie\chrysorrhoego\data\repository
mkdir -p android\app\src\main\java\com\chronie\chrysorrhoego\data\mapper

# 领域层目录
mkdir -p android\app\src\main\java\com\chronie\chrysorrhoego\domain\models
mkdir -p android\app\src\main\java\com\chronie\chrysorrhoego\domain\repository
mkdir -p android\app\src\main\java\com\chronie\chrysorrhoego\domain\usecase\wallet
mkdir -p android\app\src\main\java\com\chronie\chrysorrhoego\domain\usecase\transaction
mkdir -p android\app\src\main\java\com\chronie\chrysorrhoego\domain\usecase\cdk

# 表现层目录
mkdir -p android\app\src\main\java\com\chronie\chrysorrhoego\presentation\base
mkdir -p android\app\src\main\java\com\chronie\chrysorrhoego\presentation\util
mkdir -p android\app\src\main\java\com\chronie\chrysorrhoego\presentation\wallet
mkdir -p android\app\src\main\java\com\chronie\chrysorrhoego\presentation\transaction
mkdir -p android\app\src\main\java\com\chronie\chrysorrhoego\presentation\cdk

# 资源目录
mkdir -p android\app\src\main\res\layout\auth
mkdir -p android\app\src\main\res\layout\wallet
mkdir -p android\app\src\main\res\layout\transaction
mkdir -p android\app\src\main\res\layout\cdk

Write-Host "Project structure created successfully"