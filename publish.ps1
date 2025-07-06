param(
    [Parameter(Mandatory=$true)]
    [SecureString]$Username,
    [Parameter(Mandatory=$true)]
    [SecureString]$Password
)

$pair = "$Username`:$Password"
$BearerToken = [Convert]::ToBase64String([System.Text.Encoding]::UTF8.GetBytes($pair))

$headers = @{
    "Authorization" = "Bearer $BearerToken"
    "Accept" = "application/json"
}

$response = Invoke-RestMethod -Uri "https://ossrh-staging-api.central.sonatype.com/manual/search/repositories?profile_id=org.codehaus.gmavenplus&state=open" -Headers $headers -Method Get
if ($response.repositories.Count -ne 1) {
    Write-Error "Expected exactly 1 open repository, but found $($response.repositories.Count)."
    exit 1
}
$repoKey = $response.repositories[0].key

Invoke-RestMethod -Uri "https://ossrh-staging-api.central.sonatype.com/manual/upload/repository/$repoKey" -Headers $headers -Method Post

Write-Host "Go to https://central.sonatype.com/publishing/deployments and click 'Publish'."
