package com.esoft.dae.model

import com.esoft.dae.dao.BaseDao
import com.esoft.dae.util.{ConstantInfo,  handleUtil}
import org.apache.spark.ml.feature.VectorIndexer
import org.apache.spark.SparkContext
import org.apache.spark.sql.SQLContext
import org.json4s.NoTypeHints
import org.json4s.jackson.Serialization._
import org.json4s.jackson.Serialization
import org.apache.spark.ml.regression.{DecisionTreeRegressionModel, DecisionTreeRegressor}

/**
  * @author
  */
class DtRgs(taskId: String) extends BaseDao(taskId) {
  def execDecisionTree(sc: SparkContext, basePath: String, inputPath: String,
                                checkpointInterval: String, featuresCol: String, impurity: String,
                                labelCol: String, maxBins: String, maxDepth: String,
                                minInfoGain: String, minInstancesPerNode: String, alPath: String,
                                predictionCol: String, seed: String,
                                outputPath: String, taskId: String): Unit = {
    /////////构建yarn-spark,从parquet中读取文件
    val sqlContext = new SQLContext(sc)
    val dataFrame = sqlContext.read.parquet(basePath + inputPath)
    //        val dataFrame = csvUtil.readStrCsv(sc, sqlContext, basePath + inputPath)
    viewDf(dataFrame, "dataFrame")
    //////////////////参数处理
    //根据用户的features，先对string的col进行stringIndexer，再将indexcer的结果合成一个向量
    val modelSavePath = ConstantInfo.preModelPath + alPath
    val combinedDf = handleUtil.combineCol(handleUtil.indexFeatures(dataFrame, featuresCol, basePath + modelSavePath), handleUtil.getClassfierFeatures(featuresCol, dataFrame.dtypes))
    //对特征列进行indexer
    val dfVectorIndexer = new VectorIndexer()
      .setInputCol("features")
      .setOutputCol("indexedFeatures")
      .fit(combinedDf)
    val feaIndexedDf = dfVectorIndexer.transform(combinedDf)
    dfVectorIndexer.write.overwrite().save(basePath + modelSavePath + "/vectorIndexer")
    ///////////////将算法作用于数据集
    val dfToUse = handleUtil.fillLabelCol(feaIndexedDf, labelCol)
    viewDf(dfToUse,"dfToUse")
    val model: DecisionTreeRegressionModel = new DecisionTreeRegressor()
      .setFeaturesCol("indexedFeatures")
      .setImpurity(impurity)
      .setLabelCol(labelCol)
      .setMaxBins(maxBins.toInt)
      .setMaxDepth(maxDepth.toInt)

      .setMinInfoGain(minInfoGain.toDouble)
      .setMinInstancesPerNode(minInstancesPerNode.toInt)
      .setPredictionCol(predictionCol)
      .fit(dfToUse)

    //        .setCheckpointInterval()
    //        .setSeed(seed.toLong)
    val finalDf = model.transform(dfToUse)
    viewDf(finalDf,"finalDf")
    sc.parallelize(Seq(model), 1).saveAsObjectFile(basePath + modelSavePath + "/model")
    val summaryVO = SummaryVO(model.depth, 0, model.numFeatures, model.numNodes)

    implicit lazy val formats = Serialization.formats(NoTypeHints)
    val json = "[" + write(summaryVO) + "]"
    println(json)
    ////////////////////保存结果及保存json
    super.flagSparked(taskId.toInt, outputPath, "no head", json)
  }

  case class SummaryVO(depth: Int, numClasses: Int, numFeatures: Int, numNodes: Int)

}

object DtRgs {

  def main(args: Array[String]): Unit = {
    val basePath = ""
    val inputPath = "data/test.csv"

    val checkpointInterval = "" //Int -1或者>=1 迭代几次进行一次磁盘写操作
    val featuresCol = "col1,col2" //important String e.g. "col1,col2"  那些列作为特征列
    val impurity = "entropy" //String classification:entropy||gini   regression:variance纯度的评价标准
    val labelCol = "label" //important Stirng 标签列
    val maxBins = "32" //Int 连续变量最多可以被分为几部分
    val maxDepth = "5" //Int 树的最大深度
    val minInfoGain = "0" //double 最小的纯度改善
    val minInstancesPerNode = "1" //int 每个节点的最小实例数
    val modelSavePath = "cDecisionTree/decisionTreeModel" //String 模型的保存路径
    val predictionCol = "prediction" //important String 预测的结果输出列
    val seed = "" //long 随机种子//seed的默认值未知

    val outputPath = ""
    val taskId = "182"


    //        val args = Array(sc: SparkContext, basePath: String, inputPath: String,
    //          checkpointInterval: String, featuresCol: String, impurity: String, labelCol: String, maxBins: String,
    //          maxDepth: String, minInfoGain: String, minInstancesPerNode: String, modelSavePath: String,
    //          predictionCol: String, probabilityCol: String, rawPredictionCol: String, seed: String, thresholds: String,
    //          outputPath: String, taskId: String)

    //    val args = csvUtil.handleArg()
    //    val sc = new SparkContext(new SparkConf().setAppName("test").setMaster("local"))
    //    exec(sc, args)

    exec(handleUtil.getContext("decisionTree"), args)

  }

  def exec(sc: SparkContext, args: Array[String]): Unit = {
    implicit lazy val formats = org.json4s.jackson.Serialization.formats(org.json4s.NoTypeHints)
    case class ArgVO(inputPath: String,
                     checkpointInterval: String, featuresCol: String, impurity: String,
                     labelCol: String, maxBins: String, maxDepth: String,
                     minInfoGain: String, minInstancesPerNode: String, alPath: String,
                     predictionCol: String,  seed: String,
                     outputPath: String)

    val executer = new DtRgs(args.last)
    try {
      args.foreach { x => executer.logger.info("inArg-" + x) }
      val basePath = args(0)
      val ArgVO(inputPath: String,
      checkpointInterval: String, featuresCol: String, impurity: String,
      labelCol: String, maxBins: String, maxDepth: String,
      minInfoGain: String, minInstancesPerNode: String, alPath: String,
      predictionCol: String,  seed: String,
      outputPath: String)
      = org.json4s.jackson.Serialization.read[ArgVO](args(1))
      val taskId = args(2)
      //executer.checkArgsNum(18, args.length)
      executer.execDecisionTree(sc: SparkContext, basePath: String, inputPath: String,
        checkpointInterval: String, featuresCol: String, impurity: String,
        labelCol: String, maxBins: String, maxDepth: String,
        minInfoGain: String, minInstancesPerNode: String, alPath: String,
        predictionCol: String, seed: String,
        outputPath: String, taskId: String)


    } catch {
      case ex: Throwable => executer.handleException(args(args.length - 1).toInt, ex, "decisionTree")
    }
  }

}
