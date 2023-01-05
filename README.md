# webling_debug_tool

-----------
## webling_debug_tool 이란 무엇인가?

Webling에서 서비스중인 스냅스, 오프린트미, 오라운드는 하이브리드 앱 입니다.
각자의 팀(QA팀, 백엔드팀, 프런트팀 등등) 에서 앱에서 발생하는 로그와 데이터 또는 흐름 등을 확인하고 싶을 수 있습니다.

시각적으로 데이터의 흐름과 로그등을 앱에서 확인할 수 있는 라이브러리 입니다.

-----------

## Setup

```
repositories {
  google()
  mavenCentral()
}
```
```
 buildFeatures {
        dataBinding = true
    }
``` 

```
dependencies {
  implementation 'com.github.snaps-eddy.webling_debug_tool:final:x.y.z'
}
```
