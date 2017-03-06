/*
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
/*
 * This code was generated by https://github.com/google/apis-client-generator/
 * (build: 2017-02-15 17:18:02 UTC)
<<<<<<< HEAD
 * on 2017-03-06 at 09:18:28 UTC 
=======
 * on 2017-03-06 at 08:44:19 UTC 
>>>>>>> 4ea566368d71e64cf720b22ec524f18329cb91f6
 * Modify at your own risk.
 */

package com.example.dengxi.myapplication.backend.registration.model;

/**
 * Model definition for RegistrationRecord.
 *
 * <p> This is the Java data model class that specifies how to parse/serialize into the JSON that is
 * transmitted over HTTP when working with the registration. For a detailed explanation see:
 * <a href="https://developers.google.com/api-client-library/java/google-http-java-client/json">https://developers.google.com/api-client-library/java/google-http-java-client/json</a>
 * </p>
 *
 * @author Google, Inc.
 */
@SuppressWarnings("javadoc")
public final class RegistrationRecord extends com.google.api.client.json.GenericJson {

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String regId;

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getRegId() {
    return regId;
  }

  /**
   * @param regId regId or {@code null} for none
   */
  public RegistrationRecord setRegId(java.lang.String regId) {
    this.regId = regId;
    return this;
  }

  @Override
  public RegistrationRecord set(String fieldName, Object value) {
    return (RegistrationRecord) super.set(fieldName, value);
  }

  @Override
  public RegistrationRecord clone() {
    return (RegistrationRecord) super.clone();
  }

}
