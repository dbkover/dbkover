/** Custom scripts */
import hljs from 'highlight.js/lib/core';

import kotlin from 'highlight.js/lib/languages/kotlin';
import xml from 'highlight.js/lib/languages/xml';

hljs.registerLanguage('kotlin', kotlin);
hljs.registerLanguage('xml', xml);

document.addEventListener('DOMContentLoaded', () => {
  document.querySelectorAll('pre code').forEach((block) => {
    hljs.highlightBlock(block);
  });
});
