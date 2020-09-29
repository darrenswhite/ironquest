const merge = require('webpack-merge');
const webpack = require('webpack');
const common = require('./webpack.common.js');

module.exports = merge.mergeWithCustomize({
  customizeArray: merge.customizeArray({
    'entry.*': 'append'
  }),
  customizeObject: merge.customizeObject({
    entry: 'append'
  })
})(common.webpack, {
  mode: 'development',
  devtool: 'cheap-module-eval-source-map',
  devServer: {
    clientLogLevel: 'warning',
    contentBase: './build',
    disableHostCheck: true,
    hot: true,
    open: false,
    overlay: true,
    port: 8081,
    writeToDisk: true,
  },
  module: {
    rules: [common.tsLoader('tsconfig.json'), ...common.styleLoaders(false)],
  },
  plugins: [
    new webpack.HotModuleReplacementPlugin(),
    new webpack.NamedModulesPlugin(),
    new webpack.NoEmitOnErrorsPlugin(),
    ...common.entrypoints({
      inject: true,
    }),
  ],
});
