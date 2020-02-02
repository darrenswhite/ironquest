const merge = require('webpack-merge');
const common = require('./webpack.common.js');

module.exports = merge.smart(common, {
  mode: 'development',
  devtool: 'inline-source-map',
  devServer: {
    contentBase: './build',
    disableHostCheck: true,
    open: false,
    port: 3000,
    writeToDisk: true,
    proxy: {
      '/api': 'http://localhost:8080',
    },
  },
});
