(function (root, factory) {
  if (typeof define === 'function' && define.amd)
    define(['exports', 'kotlin'], factory);
  else if (typeof exports === 'object')
    factory(module.exports, require('kotlin'));
  else {
    if (typeof kotlin === 'undefined') {
      throw new Error("Error loading module 'kotlinx-stm-runtime'. Its dependency 'kotlin' was not found. Please, check whether 'kotlin' is loaded prior to 'kotlinx-stm-runtime'.");
    }root['kotlinx-stm-runtime'] = factory(typeof this['kotlinx-stm-runtime'] === 'undefined' ? {} : this['kotlinx-stm-runtime'], kotlin);
  }
}(this, function (_, Kotlin) {
  'use strict';
  var Kind_INTERFACE = Kotlin.Kind.INTERFACE;
  var Kind_OBJECT = Kotlin.Kind.OBJECT;
  var Kind_CLASS = Kotlin.Kind.CLASS;
  var Unit = Kotlin.kotlin.Unit;
  var Any = Object;
  var throwCCE = Kotlin.throwCCE;
  var getCallableRef = Kotlin.getCallableRef;
  var Annotation = Kotlin.kotlin.Annotation;
  var Pair = Kotlin.kotlin.Pair;
  STMSearcher$getSTM$ObjectLiteral.prototype = Object.create(STM.prototype);
  STMSearcher$getSTM$ObjectLiteral.prototype.constructor = STMSearcher$getSTM$ObjectLiteral;
  function STMContext() {
  }
  STMContext.$metadata$ = {
    kind: Kind_INTERFACE,
    simpleName: 'STMContext',
    interfaces: []
  };
  function UniversalDelegate() {
  }
  UniversalDelegate.$metadata$ = {
    kind: Kind_INTERFACE,
    simpleName: 'UniversalDelegate',
    interfaces: []
  };
  function DummySTMContext() {
    DummySTMContext_instance = this;
  }
  DummySTMContext.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'DummySTMContext',
    interfaces: [STMContext]
  };
  var DummySTMContext_instance = null;
  function DummySTMContext_getInstance() {
    if (DummySTMContext_instance === null) {
      new DummySTMContext();
    }return DummySTMContext_instance;
  }
  function DummyDelegate(t, stm) {
    this.t_91mpr4$_0 = t;
    this.stm_qrm520$_0 = stm;
  }
  Object.defineProperty(DummyDelegate.prototype, 'stm', {
    get: function () {
      return this.stm_qrm520$_0;
    }
  });
  DummyDelegate.prototype.unpack_acdebo$ = function (ctx) {
    return this.t_91mpr4$_0;
  };
  DummyDelegate.prototype.unpackTransactional_acdebo$ = function (ctx) {
    return this.unpack_acdebo$(ctx);
  };
  DummyDelegate.prototype.pack_bebfq9$ = function (value, ctx) {
    this.t_91mpr4$_0 = value;
  };
  DummyDelegate.prototype.packTransactional_bebfq9$ = function (value, ctx) {
    this.pack_bebfq9$(value, ctx);
  };
  DummyDelegate.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'DummyDelegate',
    interfaces: [UniversalDelegate]
  };
  function STM() {
  }
  STM.prototype.beforeTransaction_odnb7h$ = function (context, block) {
  };
  STM.prototype.runAtomically_odnb7h$ = function (context, block) {
    if (context === void 0)
      context = null;
    var tmp$;
    this.beforeTransaction_odnb7h$(context, block);
    while (true) {
      var tmp$_0 = this.tryCommitTransaction_odnb7h$(context, block);
      var res = tmp$_0.component1()
      , ok = tmp$_0.component2();
      if (ok)
        return (tmp$ = res) == null || Kotlin.isType(tmp$, Any) ? tmp$ : throwCCE();
    }
  };
  function STM$getVar$lambda(closure$delegate) {
    return function ($receiver) {
      return closure$delegate.unpackTransactional_acdebo$($receiver);
    };
  }
  STM.prototype.getVar_cqzugg$ = function (context, delegate) {
    var tmp$;
    return (tmp$ = context != null ? getCallableRef('unpack', function ($receiver, p1) {
      return $receiver.unpack_acdebo$(p1);
    }.bind(null, delegate))(context) : null) != null ? tmp$ : this.runAtomically_odnb7h$(void 0, STM$getVar$lambda(delegate));
  };
  function STM$setVar$lambda(closure$delegate, closure$newValue) {
    return function ($receiver) {
      closure$delegate.packTransactional_bebfq9$(closure$newValue, $receiver);
      return Unit;
    };
  }
  STM.prototype.setVar_rwee22$ = function (context, delegate, newValue) {
    if (context === void 0)
      context = null;
    var tmp$;
    var tmp$_0;
    if (context != null) {
      delegate.packTransactional_bebfq9$(newValue, context);
      tmp$_0 = Unit;
    } else
      tmp$_0 = null;
    (tmp$ = tmp$_0) != null ? tmp$ : this.runAtomically_odnb7h$(void 0, STM$setVar$lambda(delegate, newValue));
  };
  STM.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'STM',
    interfaces: []
  };
  function SharedMutable() {
  }
  SharedMutable.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'SharedMutable',
    interfaces: [Annotation]
  };
  function AtomicFunction() {
  }
  AtomicFunction.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'AtomicFunction',
    interfaces: [Annotation]
  };
  function runAtomically(stm, block) {
    if (stm === void 0)
      stm = STMSearcher_getInstance().getSTM();
    return stm.runAtomically_odnb7h$(null, block);
  }
  function STMSearcher() {
    STMSearcher_instance = this;
  }
  function STMSearcher$getSTM$ObjectLiteral() {
    STM.call(this);
  }
  STMSearcher$getSTM$ObjectLiteral.prototype.getContext = function () {
    return DummySTMContext_getInstance();
  };
  STMSearcher$getSTM$ObjectLiteral.prototype.tryCommitTransaction_odnb7h$ = function (transactionContext, block) {
    return new Pair(block(DummySTMContext_getInstance()), true);
  };
  STMSearcher$getSTM$ObjectLiteral.prototype.wrap_mh5how$ = function (initValue) {
    return new DummyDelegate(initValue, this);
  };
  STMSearcher$getSTM$ObjectLiteral.$metadata$ = {
    kind: Kind_CLASS,
    interfaces: [STM]
  };
  STMSearcher.prototype.getSTM = function () {
    return new STMSearcher$getSTM$ObjectLiteral();
  };
  STMSearcher.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'STMSearcher',
    interfaces: []
  };
  var STMSearcher_instance = null;
  function STMSearcher_getInstance() {
    if (STMSearcher_instance === null) {
      new STMSearcher();
    }return STMSearcher_instance;
  }
  var package$kotlinx = _.kotlinx || (_.kotlinx = {});
  var package$stm = package$kotlinx.stm || (package$kotlinx.stm = {});
  package$stm.STMContext = STMContext;
  package$stm.UniversalDelegate = UniversalDelegate;
  Object.defineProperty(package$stm, 'DummySTMContext', {
    get: DummySTMContext_getInstance
  });
  package$stm.DummyDelegate = DummyDelegate;
  package$stm.STM = STM;
  package$stm.SharedMutable = SharedMutable;
  package$stm.AtomicFunction = AtomicFunction;
  package$stm.runAtomically_gtc3jh$ = runAtomically;
  Object.defineProperty(package$stm, 'STMSearcher', {
    get: STMSearcher_getInstance
  });
  Kotlin.defineModule('kotlinx-stm-runtime', _);
  return _;
}));

//# sourceMappingURL=kotlinx-stm-runtime.js.map
