function s(e){try{return btoa(encodeURIComponent(e).replace(/%([0-9A-F]{2})/g,function(t,r){return String.fromCharCode("0x"+r)}))}catch(t){return console.warn("Base64编码失败，使用URL编码:",t),encodeURIComponent(e)}}function f(e=300,t=200,r=`${e}×${t}`,o="#f5f5f5",a="#999"){const n=`
    <svg width="${e}" height="${t}" xmlns="http://www.w3.org/2000/svg">
      <rect width="100%" height="100%" fill="${o}"/>
      <text x="50%" y="50%" font-family="Arial, sans-serif" font-size="16" 
            fill="${a}" text-anchor="middle" dominant-baseline="middle">
        ${r}
      </text>
    </svg>
  `;try{return`data:image/svg+xml;base64,${s(n)}`}catch(c){return console.warn("SVG Base64编码失败，使用URL编码:",c),`data:image/svg+xml;charset=utf-8,${encodeURIComponent(n)}`}}function l(e=300,t=200){return f(e,t,"文章封面","#e8f4fd","#409eff")}function g(e="用户",t=40){const r=e.charAt(0).toUpperCase(),o=[{bg:"#409eff",text:"#fff"},{bg:"#67c23a",text:"#fff"},{bg:"#e6a23c",text:"#fff"},{bg:"#f56c6c",text:"#fff"},{bg:"#909399",text:"#fff"}],a=e.charCodeAt(0)%o.length,n=o[a];return f(t,t,r,n.bg,n.text)}export{g as a,f as b,l as g};
