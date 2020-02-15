const path = require('path');
const webpack = require('webpack');
const { CleanWebpackPlugin } = require('clean-webpack-plugin');
const HtmlWebpackPlugin = require('html-webpack-plugin');
const MiniCssExtractPlugin = require('mini-css-extract-plugin');
const VueLoaderPlugin = require('vue-loader/lib/plugin');
const _ = require('lodash');

const WEB = {
  target: 'web',
  entrypoints: {
    index: {
      path: path.resolve('src', 'web', 'pages'),
      html: 'index.html',
      ts: 'Index.ts',
    },
  },
};

const OVERWOLF = {
  target: 'overwolf',
  entrypoints: {
    controller: {
      path: path.resolve('src', 'overwolf', 'windows', 'controller'),
      html: 'controller.html',
      ts: 'Controller.ts',
    },
    results: {
      path: path.resolve('src', 'overwolf', 'windows', 'results'),
      html: 'results.html',
      ts: 'Results.ts',
    },
    settings: {
      path: path.resolve('src', 'overwolf', 'windows', 'settings'),
      html: 'settings.html',
      ts: 'Settings.ts',
    },
    username: {
      path: path.resolve('src', 'overwolf', 'windows', 'username'),
      html: 'username.html',
      ts: 'Username.ts',
    },
  },
};

let config;

if (process.env.TARGET === WEB.target) {
  config = WEB;
} else if (process.env.TARGET === OVERWOLF.target) {
  config = OVERWOLF;
} else {
  throw new Error(`Unknown target: ${process.env.TARGET}`);
}

module.exports = {
  entry: _.mapValues(config.entrypoints, e => path.resolve(e.path, e.ts)),
  module: {
    rules: [
      {
        test: /\.tsx?$/,
        loader: 'ts-loader?configFile=tsconfig.json',
        exclude: /node_modules|vue\/src/,
        options: {
          appendTsSuffixTo: [/\.vue$/],
        },
      },
      {
        test: /\.(png|woff|woff2|eot|ttf|svg)$/,
        use: 'file-loader?limit=1024&name=[path][name].[ext]',
      },
      {
        test: /\.vue$/,
        loader: 'vue-loader',
      },
      {
        test: /\.(s(c|a)ss|css)$/,
        use: [
          'vue-style-loader',
          'css-loader',
          {
            loader: 'sass-loader',
            options: {
              implementation: require('sass'),
              fiber: require('fibers'),
            },
            options: {
              implementation: require('sass'),
              sassOptions: {
                fiber: require('fibers'),
              },
            },
          },
        ],
      },
      {
        test: /\.styl(us)?$/,
        use: [
          'vue-style-loader',
          MiniCssExtractPlugin.loader,
          'css-loader',
          'stylus-loader',
        ],
      },
    ],
  },
  plugins: [
    new CleanWebpackPlugin(),
    ..._.map(config.entrypoints, e => {
      return new HtmlWebpackPlugin({
        filename: e.html,
        template: path.resolve(e.path, e.html),
        chunks: [e.html.replace('.html', '')],
      });
    }),
    new webpack.HashedModuleIdsPlugin(),
    new webpack.ProvidePlugin({
      $: 'jquery',
      jQuery: 'jquery',
      'window.jQuery': 'jquery',
    }),
    new VueLoaderPlugin(),
    new MiniCssExtractPlugin({
      filename: '[name].[hash].css',
      chunkFilename: '[id].[hash].css',
    }),
  ],
  resolve: {
    alias: {
      vue: 'vue/dist/vue.common.js',
    },
    extensions: ['.tsx', '.ts', '.js', '.styl'],
  },
  output: {
    filename: '[name].[contenthash].js',
    path: path.resolve(__dirname, 'build'),
  },
};
