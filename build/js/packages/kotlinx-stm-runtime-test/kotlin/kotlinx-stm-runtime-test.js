(function (root, factory) {
  if (typeof define === 'function' && define.amd)
    define(['exports', 'kotlin', 'kotlin-test'], factory);
  else if (typeof exports === 'object')
    factory(module.exports, require('kotlin'), require('kotlin-test'));
  else {
    if (typeof kotlin === 'undefined') {
      throw new Error("Error loading module 'kotlinx-stm-runtime-test'. Its dependency 'kotlin' was not found. Please, check whether 'kotlin' is loaded prior to 'kotlinx-stm-runtime-test'.");
    }if (typeof this['kotlin-test'] === 'undefined') {
      throw new Error("Error loading module 'kotlinx-stm-runtime-test'. Its dependency 'kotlin-test' was not found. Please, check whether 'kotlin-test' is loaded prior to 'kotlinx-stm-runtime-test'.");
    }root['kotlinx-stm-runtime-test'] = factory(typeof this['kotlinx-stm-runtime-test'] === 'undefined' ? {} : this['kotlinx-stm-runtime-test'], kotlin, this['kotlin-test']);
  }
}(this, function (_, Kotlin, $module$kotlin_test) {
  'use strict';
  var Kind_CLASS = Kotlin.Kind.CLASS;
  var test = $module$kotlin_test.kotlin.test.test;
  var suite = $module$kotlin_test.kotlin.test.suite;
  function SampleTests() {
  }
  SampleTests.prototype.testMe = function () {
  };
  SampleTests.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'SampleTests',
    interfaces: []
  };
  function SampleTestsJS() {
  }
  SampleTestsJS.prototype.testHello = function () {
  };
  SampleTestsJS.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'SampleTestsJS',
    interfaces: []
  };
  var package$kotlinx = _.kotlinx || (_.kotlinx = {});
  var package$stm = package$kotlinx.stm || (package$kotlinx.stm = {});
  package$stm.SampleTests = SampleTests;
  package$stm.SampleTestsJS = SampleTestsJS;
  suite('kotlinx.stm', false, function () {
    suite('SampleTests', false, function () {
      test('testMe', false, function () {
        return (new SampleTests()).testMe();
      });
    });
    suite('SampleTestsJS', false, function () {
      test('testHello', false, function () {
        return (new SampleTestsJS()).testHello();
      });
    });
  });
  Kotlin.defineModule('kotlinx-stm-runtime-test', _);
  return _;
}));

//# sourceMappingURL=kotlinx-stm-runtime-test.js.map
