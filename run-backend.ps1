    $ErrorActionPreference = "Stop"

    Write-Host "Appraisal Spring Boot backend startup"
    $secure = Read-Host "Enter MySQL password for DB user root" -AsSecureString
    $ptr = [Runtime.InteropServices.Marshal]::SecureStringToBSTR($secure)
    try {
        $env:DB_USERNAME = "root"
        $env:DB_PASSWORD = [Runtime.InteropServices.Marshal]::PtrToStringBSTR($ptr)
        if ([string]::IsNullOrEmpty($env:DB_PASSWORD)) {
            throw "No password entered. Set DB_PASSWORD and try again."
        }
        if (-not $env:DB_URL) {
            $env:DB_URL = "jdbc:mysql://localhost:3306/appraisel_db"
        }
        .\mvnw.cmd spring-boot:run
    }
    finally {
        if ($ptr -ne [IntPtr]::Zero) {
            [Runtime.InteropServices.Marshal]::ZeroFreeBSTR($ptr)
        }
        Remove-Item Env:DB_PASSWORD -ErrorAction SilentlyContinue
    }
