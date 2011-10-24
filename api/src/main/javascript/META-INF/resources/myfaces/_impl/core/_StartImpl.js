/* Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to you under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/*we cannot privatize with a global function hence we store the values away for the init part*/
(function() {
    var target = window || document.body;
    if (target.myfaces && target.myfaces._impl) {
        //some mobile browsers do not have a window object

        var _RT = myfaces._impl.core._Runtime;
        var impl = "myfaces._impl.";
        var params = {_MF_CLS: _RT.extendClass,
            _MF_SINGLTN: _RT.singletonExtendClass,
            _PFX_UTIL: impl + "_util.",
            _PFX_CORE:impl + "core.",
            _PFX_XHR: impl + "xhrCore.",
            _PFX_I18N: impl + "i18n."};

        for (var key in params) {
            _RT[key] = target[key];
            target[key] = params[key];
        }
    }
})();