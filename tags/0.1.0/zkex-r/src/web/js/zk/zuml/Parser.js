(function(){function g(k){var h=k.innerHTML,i=h.indexOf("<!--");if(i>=0){h=h.substring(i+4,h.lastIndexOf("-->"))}h=h.trim();return h?"<div>"+h.trim()+"</div>":null}function e(v,s,o,h){var t;if(!v||!(t=v.firstChild)){return}do{var m=t.nextSibling;v.removeChild(t);s.push(t);t=m}while(t);var q=s.length;if(!h||h.replaceHTML!==false){var u=[o];if(q>1){var i=o.parentNode,m=o.nextSibling;for(var r=q;--r>0;){var k=document.createElement("DIV");u.push(k);i.insertBefore(k,m)}}for(var r=0;r<q;++r){s[r].replaceHTML(u[r])}}return s.length<=1?s[0]:s}function a(k){var h={},j=[];d(k,h);for(var i in h){j.push(i)}return j.join(",")}function d(n,i){var h=n.tagName;if("zk"!=h&&"attribute"!=h){if(!zk.Widget.getClass(h)){var m=zk.wgt.WidgetInfo.getClassName(h);if(!m){throw"Unknown tag: "+h}var k=m.lastIndexOf(".");if(k>=0){i[m.substring(0,k)]=true}}for(n=n.firstChild;n;n=n.nextSibling){var l=n.nodeType;if(l==1){d(n,i)}}}}function f(m,o,k,n){if(!o){return null}var q=b(m,o.getAttribute("forEach"),k);if(q!=null){var p=window.each;for(var h=q.length,i=0;i<h;i++){window.each=q[i];c(m,o,k,n)}window.each=p}else{c(m,o,k,n)}}function c(n,y,i,x){var u=b(n,y.getAttribute("if"),i),z=b(n,y.getAttribute("unless"),i);if((u==null||u)&&(z==null||!z)){var k=y.tagName,m;if("zk"==k){m=n}else{if("attribute"==k){var o=b(n,y.getAttribute("name"),i);if(!o){throw"The name attribute required, "+y}n.set(o,zk.xml.Utl.getElementValue(y));return}else{var r=y.attributes;m=zk.Widget.newInstance(k);if(x){x.push(m)}if(n){n.appendChild(m)}for(var t=r.length,v=0;v<t;++v){var s=r[v];m.set(s.name,b(m,s.value,i))}}}var A;for(y=y.firstChild;y;y=y.nextSibling){var B=y.nodeType;if(B==1){var h=[];f(m,y,i,h);if(A&&(h=h[0])){h.prolog=A;A=null}}else{if(B==3){var q=b(m,y.nodeValue,i);if(q.trim().length){var p=new zk.Native();p.prolog=q;m.appendChild(p)}else{if(m.blankPreserved){A=q}}}}}}}function b(v,w,o){if(w){for(var m=0,i,h,u,r=w.length-1,q;;){i=w.indexOf("#{",m);if(i<0){i=w.indexOf("${",m);if(i<0){if(q){w=q+w.substring(m)}break}}u=w.substring(m,i);h=w.indexOf("}",i+2);if(h<0){w=q?q+u:u;break}q=q?q+u:u;u=w.substring(i+2,h);try{var p=new Function("var _=arguments[0];return "+u);u=v?p.call(v,o):p(o)}catch(n){throw"Failed to evaluate "+u}if(!q&&h==r){return u}if(u){q+=u}m=h+1}}return w}zk.zuml.Parser={create:function(j,l,h,i){if(typeof h=="function"&&!i){i=h;h=null}l=(typeof l=="string"?zk.xml.Utl.parseXML(l):l).documentElement;var k=[];zk.load(a(l),function(){f(j,l,h,k);if(i){i(k.length<=1?k[0]:k)}});return k.length<=1?k[0]:k},createAt:function(l,k,i,j){if(typeof i=="function"&&!j){j=i;i=null}l=jq(l)[0];var h=g(l);if(!h){return}var m=[],n=zk.zuml.Parser.create(null,h,i,function(o){o=e(o,m,l,k);if(j){j(o)}});return e(n,m,l,k)}}})();