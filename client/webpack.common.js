const path = require('path');
const webpack = require('webpack');
const { CleanWebpackPlugin } = require('clean-webpack-plugin');
const CopyWebpackPlugin = require('copy-webpack-plugin');
const HtmlWebpackPlugin = require('html-webpack-plugin');
const MiniCssExtractPlugin = require('mini-css-extract-plugin');
const VueLoaderPlugin = require('vue-loader/lib/plugin');
const { filter, keys, map, mapValues } = require('lodash');

const CONFIGS = {
  web: {
    entrypoints: {
      index: path.resolve(__dirname, 'src', 'web', 'index.ts'),
    },
  },
  overwolf: {
    entrypoints: {
      controller: path.resolve(
        __dirname,
        'src',
        'overwolf',
        'windows',
        'controller',
        'index.ts'
      ),
      results: path.resolve(
        __dirname,
        'src',
        'overwolf',
        'windows',
        'results',
        'index.ts'
      ),
      settings: path.resolve(
        __dirname,
        'src',
        'overwolf',
        'windows',
        'settings',
        'index.ts'
      ),
      username: path.resolve(
        __dirname,
        'src',
        'overwolf',
        'windows',
        'username',
        'index.ts'
      ),
    },
  },
};

const APIS = {
  local: 'window.location.href',
  external: JSON.stringify('https://iron-quest.herokuapp.com'),
};

const config = CONFIGS[process.env.TARGET] || CONFIGS.web;
const api = APIS[process.env.API] || APIS.external;

function entrypoints(options) {
  return map(config.entrypoints, (entrypoint, name) => {
    return new HtmlWebpackPlugin({
      filename: name + '.html',
      template: path.resolve(__dirname, 'src', 'index.html'),
      excludeChunks: filter(keys(config.entrypoints), key => key !== name),
      ...options,
    });
  });
}

function styleLoaders(extract) {
  const styleLoader = extract
    ? MiniCssExtractPlugin.loader
    : 'vue-style-loader';
  const cssLoader = {
    loader: 'css-loader',
    options: {
      sourceMap: true,
    },
  };
  const postCssLoader = {
    loader: 'postcss-loader',
    options: {
      sourceMap: true,
    },
  };

  return [
    {
      test: /\.(s(c|a)ss)$/,
      use: [
        styleLoader,
        cssLoader,
        postCssLoader,
        {
          loader: 'sass-loader',
          options: {
            sourceMap: true,
          },
        },
      ],
    },
    {
      test: /\.styl(us)?$/,
      use: [
        styleLoader,
        cssLoader,
        postCssLoader,
        {
          loader: 'stylus-loader',
          options: {
            sourceMap: true,
          },
        },
      ],
    },
    {
      test: /\.css$/,
      use: [styleLoader, cssLoader, postCssLoader],
    },
  ];
}

function tsLoader(configFile) {
  return {
    test: /\.tsx?$/,
    loader: 'ts-loader',
    exclude: /node_modules|vue\/src/,
    options: {
      appendTsSuffixTo: [/\.vue$/],
      configFile: configFile,
    },
  };
}

module.exports = {
  entrypoints,
  styleLoaders,
  tsLoader,
  webpack: {
    entry: config.entrypoints,
    module: {
      rules: [
        {
          test: /\.(png|woff|woff2|eot|ttf|svg)$/,
          use: 'file-loader?limit=1024&name=[path][name].[ext]',
        },
        {
          test: /\.vue$/,
          loader: 'vue-loader',
        },
      ],
    },
    plugins: [
      new CleanWebpackPlugin(),
      new VueLoaderPlugin(),
      new webpack.DefinePlugin({
        __API__: api,
      }),
      new CopyWebpackPlugin([
        {
          from: path.resolve(__dirname, 'assets/icon-16x16.ico'),
          to: path.resolve(__dirname, 'build/favicon.ico'),
        },
      ]),
    ],
    resolve: {
      alias: {
        vue$: 'vue/dist/vue.esm.js',
        '@': path.resolve(__dirname, 'src'),
      },
      extensions: ['.tsx', '.ts', '.js', '.styl', '.vue'],
    },
    output: {
      filename: 'js/[name].js',
      path: path.resolve(__dirname, 'build'),
    },
    node: {
      setImmediate: false,
      dgram: 'empty',
      fs: 'empty',
      net: 'empty',
      tls: 'empty',
      child_process: 'empty',
    },
  },
};
