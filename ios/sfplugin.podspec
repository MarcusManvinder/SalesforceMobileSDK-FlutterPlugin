#
# To learn more about a Podspec see http://guides.cocoapods.org/syntax/podspec.html
#
Pod::Spec.new do |s|
  s.name             = 'sfplugin'
  s.version          = '10.2.0'
  s.summary          = 'Flutter plugin for the Salesforce Mobile SDK.'
  s.description      = 'Flutter plugin for the Salesforce Mobile SDK.'
  s.homepage         = "https://github.com/forcedotcom/SalesforceMobileSDK-iOS.git"
  s.license      = { :type => "Salesforce.com Mobile SDK License", :file => "LICENSE.md" }
  s.author       = { "Kevin Hawkins" => "khawkins@salesforce.com" }
  s.source           = { :path => '.' }
  s.source_files = 'Classes/**/*'
  s.public_header_files = 'Classes/**/*.h'
  s.dependency 'Flutter'
  s.dependency 'MobileSync'
  s.dependency 'SmartStore'
  s.dependency 'SalesforceSDKCore'
  s.dependency 'SalesforceAnalytics'

  s.ios.deployment_target = '10.0'
end

