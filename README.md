# debug_tool

-----------
## debug_tool 이란 무엇인가?

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
dependencies {
  implementation 'io.github.snaps-eddy:visibledebugtool:x.y.z'
}
```

## How to use

#### Balloon supports both Kotlin and Java projects, so you can reference it by your language.

### Create DebugTool with Kotlin

#### We can create an instance of the DebugTool with the DebugTool.Builder class.

```
   val debugTool = DebugTool.Builder(this)
            .setSearchKeyWord("test")
            .setAutoPermissionCheck(true)
            .build()

``` 


### Create DebugTool with Kotlin DSL

#### We can also create an instance of the DebugTool with the Kotlin DSL.

<details>
<summary>Keep reading for more details</summary>

#### You can create an instance of the DebugTool as the following example below:

```
 val debugTool = createDebugTool(this) {
     setSearchKeyWord("test")
     setAutoPermissionCheck(true)
 }

``` 

</details>



### Create DebugTool with Java

```
    DebugTool debugTool = new DebugTool.Builder(context)    //create 
            .setAutoPermissionCheck(true)
            .setSearchKeyWord("test")
            .build();

``` 

## Show up DebugTool

```
debugTool.bindService()

``` 

## Dismiss DebugTool

```
debugTool.unbindService()

``` 

## Customize
### VisibleDebugTool supports the following methods.

- `setSearchKeyWord(String)`
- `setSearchKeyWordList(List<String>)`
- `setAutoPermissionCheck(Boolean)`

## License

```
   Copyright 2023 Jang.Eddy

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

``` 


